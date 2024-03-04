package fpt.edu.vn.exagen.APIService;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SendRequestTasks extends AsyncTask<String, Void, ApiResponse> {

    private final ApiInterface apiInterface;
    private final SendRequestListener listener;

    public SendRequestTasks(ApiInterface apiInterface, SendRequestListener listener) {
        this.apiInterface = apiInterface;
        this.listener = listener;
    }

    @Override
    protected ApiResponse doInBackground(String... strings) {
        String base64Image = strings[0];
        try {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("Image", base64Image);
            Log.d("SendRequestTask", "jsonObject: " + jsonObject);
            Call<ApiResponse> call = apiInterface.sendImage(jsonObject);
            Response<ApiResponse> response = call.execute();
            Log.d("SendRequestTask", "response: " + response);

            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("SendRequestTask", "response: " + response.code());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(ApiResponse apiResponse) {
        super.onPostExecute(apiResponse);
        Log.d("SendRequestTask", "onPostExecute: " + apiResponse);
        if (apiResponse != null) {
            listener.onRequestSuccess(apiResponse);
        } else {
            listener.onRequestFailure();
        }
    }
    public interface SendRequestListener {
        void onRequestSuccess(ApiResponse response);
        void onRequestFailure();
    }
}



