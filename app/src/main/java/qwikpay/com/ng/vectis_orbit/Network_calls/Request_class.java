package qwikpay.com.ng.vectis_orbit.Network_calls;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Request_class {




    @POST("Cravetech/AddHandler")
    Call<Map<String,Object>> START_VN(@Body Map<String,Object> data);







}
