package com.example.macbookpro.zenweshare;

/**
 * Created by macbookpro on 17/02/2015.
 */
public interface FetchTask {
    String postData(String valueIWantToSend);

    void onPostExecute(String result);
}
