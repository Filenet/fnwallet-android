package io.filenet.wallet.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import io.filenet.wallet.BuildConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploader {
    public static void upload(String path, String name) {
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        File file = new File(path);
        String url = BuildConfig.APIHOST + "/upload";
        RequestBody requestBody = new MultipartBody.Builder().
                addFormDataPart("file", name.toLowerCase(), RequestBody.create(type, file)).build();
        Request request1 = new Request.Builder().url(url).post(requestBody).build();
        Call call = client.newCall(request1);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    int status = jobj.getInt("status");
                    if (status == 200) {
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
