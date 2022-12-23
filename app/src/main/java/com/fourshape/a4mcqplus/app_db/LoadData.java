package com.fourshape.a4mcqplus.app_db;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class LoadData {

    private final Context context;
    private LoadDataListener loadDataListener;

    public LoadData (Context context) {
        this.context = context;
    }

    public void setLoadDataListener (LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public void sendRequest (String requestParam) {
        new FetchDataFromDB(context,loadDataListener).execute(requestParam);
    }

    private static class FetchDataFromDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> weakReference;
        private LoadDataListener loadDataListener;

        public FetchDataFromDB (Context context, LoadDataListener loadDataListener) {
            this.weakReference = new WeakReference<>(context);
            this.loadDataListener = loadDataListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loadDataListener != null)
                loadDataListener.onResult(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            DBHandler dbHandler = new DBHandler(weakReference.get());
            String data = dbHandler.loadData(strings[0]);
            dbHandler.close();

            return data;
        }
    }

}
