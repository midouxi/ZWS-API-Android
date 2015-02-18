<?php

class ZWS_Exception extends Exception {

}


class ZWS_Client {

    private $_version;
    private $_id;
    private $_key;
    private $_token;
    private $_rawResults;

    public function __construct($version)
    {
        $this->_version = $version;
    }

    private function _launch($vars, $method) {

        if(!isset($this->_token)) {
            $authResponse = $this->_auth();
            if(!isset($authResponse->app_token)) {
                return $authResponse;
            } else {
                $this->_token = $authResponse->app_token;
            }
        }

        if(!isset($vars['endPoint'])) {
            throw new ZWS_Exception('Missing endpoint parameter');
        }

        $url = $this->_buildRequestUrl($vars['endPoint']);

        $params = !isset($vars['params']) ? array() : $vars['params'];
        $params = array_merge($params, array(
            'appToken' => $this->_token
            )
        );

        return $this->_sendRequest($url, $method, $params);
    }

    private function _auth() {

        if(!isset($this->_id)) {
            throw new ZWS_Exception('Client ID should be defined with method ZWS_Client::setClientId');
        }
        if(!isset($this->_key)) {
            throw new ZWS_Exception('Private key should be defined with method ZWS_Client::setSecretKey');
        }

        $url = $this->_buildRequestUrl('/auth');
        $url .= '?client_id='.$this->_id.'&secret_key='.$this->_key;

        return $this->_sendRequest($url, 'get', array());
    }

    private function _buildRequestUrl($endPoint) {
        return 'http://127.0.0.1/api/'.$this->_version.$endPoint;
    }

    private function _sendRequest($url, $method, $vars) {
        $ch = curl_init();
        curl_setopt_array($ch, array(
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_HEADER         => false,
            CURLOPT_FOLLOWLOCATION => false,
            CURLOPT_SSL_VERIFYHOST => 0,
            )
        );

        if($method != 'get') {
            curl_setopt($ch, CURLOPT_POST, TRUE);
            curl_setopt($ch, CURLOPT_POSTFIELDS, array_merge($vars, array(
                '_method' => $method
                ))
            );
        } else {
            $url .= (strpos($url, '?') ? '&' : '?').http_build_query($vars);
        }
        curl_setopt($ch, CURLOPT_URL, $url);

        return $this->_handleResponse($ch);
    }

    private function _handleResponse($ch) {
        $this->_rawResults = curl_exec($ch);
        $response = json_decode($this->_rawResults);
        $response = is_null($response) ? new stdClass() : $response;
        if(!isset($reponse->http_code)) {
            $response->http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        }

        if($response->http_code != '200' && !isset($response->error)) {
            switch ($response->http_code) {
                case '400':
                $response->error = 'Bad Request';
                break;

                case '401':
                $response->error = 'Unauthorized';
                break;

                case '403':
                $response->error = 'Forbidden';
                break;

                case '404':
                $response->error = 'Not Found';
                break;

                case '405':
                $response->error = 'Method Not Allowed';
                break;

                case '500':
                $response->error = 'Internal Server Error';
                break;

                case '501':
                $response->error = 'Not Implemented';
                break;

                case '503':
                $response->error = 'Service Unavailable';
                break;

                default:
                $response->error = 'Unknown error';
                break;
            }
        }
        return $response;
    }

    public function getRawResults() {
        return $this->_rawResults;
    }

    public function setClientId($id) {
        $this->_id = $id;
    }

    public function setSecretKey($key) {
        $this->_key = $key;
    }

    public function get($vars) {
        return $this->_launch($vars, 'get');
    }

    public function post($vars) {
        return $this->_launch($vars, 'post');
    }

    public function put($vars) {
        return $this->_launch($vars, 'put');
    }

    public function delete($vars) {
        return $this->_launch($vars, 'delete');
    }
}
