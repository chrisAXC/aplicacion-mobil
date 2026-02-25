package com.example.tiendaandroid.network;

import com.example.tiendaandroid.models.CarritoItem;
import com.example.tiendaandroid.models.Producto;
import com.example.tiendaandroid.models.Usuario;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ============= PRODUCTOS =============
    @GET("productos")
    Call<List<Producto>> getProductos();

    @GET("productos/{id}")
    Call<Producto> getProductoById(@Path("id") int id);

    // ============= USUARIOS =============
    // ✅ CAMBIADO: Ahora acepta Map para registro (como lo usa RegisterActivity)
    @POST("usuarios/registro")
    Call<Map<String, Object>> registrarUsuario(@Body Map<String, Object> usuario);

    @POST("usuarios/login")
    Call<Map<String, Object>> loginUsuario(@Body Map<String, String> credenciales);

    // ============= CARRITO =============
    @GET("carrito/{id_usuario}")
    Call<List<CarritoItem>> getCarrito(@Path("id_usuario") int idUsuario);

    @POST("carrito/add")
    Call<Map<String, Object>> agregarAlCarrito(@Body Map<String, Object> item);

    @PUT("carrito/update")
    Call<Map<String, Object>> actualizarCantidad(@Body Map<String, Object> datos);

    @DELETE("carrito/remove/{id_usuario}/{id_producto}")
    Call<Map<String, Object>> eliminarDelCarrito(@Path("id_usuario") int idUsuario,
                                                 @Path("id_producto") int idProducto);

    @DELETE("carrito/clear/{id_usuario}")
    Call<Map<String, Object>> vaciarCarrito(@Path("id_usuario") int idUsuario);
}