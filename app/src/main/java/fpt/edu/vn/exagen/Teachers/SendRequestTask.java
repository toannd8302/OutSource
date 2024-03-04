package fpt.edu.vn.exagen.Teachers;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import fpt.edu.vn.exagen.APIService.ApiInterface;
import fpt.edu.vn.exagen.APIService.ApiResponse;
import retrofit2.Call;
import retrofit2.Response;

public class SendRequestTask extends AsyncTask<String, Void, ApiResponse> {

    private final ApiInterface apiInterface;
    private final SendRequestListener listener;

    public SendRequestTask(ApiInterface apiInterface, SendRequestListener listener) {
        this.apiInterface = apiInterface;
        this.listener = listener;
    }

    @Override
    protected ApiResponse doInBackground(String... strings) {
        String base64Image = strings[0];

        try {
            // Tạo JSON Object để chứa dữ liệu cần gửi đến API
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("Image", base64Image);
    Log.d("SendRequestTask", "jsonObject: " + jsonObject);
            // Gửi yêu cầu POST đến API và nhận kết quả
            Call<ApiResponse> call = apiInterface.sendImage(jsonObject);
            Response<ApiResponse> response = call.execute();
         Log.d("SendRequestTask", "response: " + response);

            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Xử lý trường hợp không thành công
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


