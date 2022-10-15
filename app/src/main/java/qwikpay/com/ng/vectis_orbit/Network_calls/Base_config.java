package qwikpay.com.ng.vectis_orbit.Network_calls;

import qwikpay.com.ng.vectis_orbit.UTILS.Constant;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Base_config {


    static Retrofit retrofit;

    //Issues with timeout
    public  static  Retrofit getRetrofit(){
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(20, TimeUnit.MINUTES)
//                .readTimeout(20,TimeUnit.MINUTES)
//                .writeTimeout(20,TimeUnit.MINUTES)
//                .build();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.CLOUD_BASE_URL)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return  retrofit;
    }
}
