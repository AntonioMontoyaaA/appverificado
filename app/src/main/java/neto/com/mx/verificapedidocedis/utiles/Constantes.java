package neto.com.mx.verificapedidocedis.utiles;

/**
 * Created by yruizm on 13/01/17.
 */

public class Constantes {

    public static final String URL_STRING_LOGIN = "http://10.81.12.45:7777/WSSIAN/services/WSPickeoMovil/";//DESA
    public static final String URL_STRING = "http://10.81.12.45:7777/WSSIAN/services/WSVerificadorMovil/";//DESA.

    //public static final String URL_STRING_LOGIN = "http://10.81.12.46:7777/WSSIANPAR/services/WSPickeoMovil/";//QA
    //public static final String URL_STRING = "http://10.81.12.46:7777/WSSIANPAR/services/WSVerificadorMovil/";//QA
    //public static final String URL_STRING_LOGIN = "http://10.81.12.203:8003/WSSIONIndicadores/services/WSPickeoMovil/";//PROD
    //public static final String URL_STRING = "http://10.81.12.203:8003/WSSIONIndicadores/services/WSVerificadorMovil/";//PROD

    //Cifradas
    // public static final String URL_STRING = "http://10.37.140.202:7777/WSSIANMoviles/services/WSAbastoMovil/"; //DESA
    //
    // public static final String URL_STRING = "http://10.81.12.46:7777/appWSSIANMovilesPAR/services/WSAbastoMovil/";//QA
     //public static final String URL_STRING = "https://200.38.108.77/WSSIANMoviles/services/WSAbastoMovil/";//PROD

    public static final int CONTADOR_GUARDA_AVANCE = 10;

    public static final String LLAVE_PRIVADA = "RecibePedidosV1";

    public static final String ID_APP_VERIFICADOR   =   "2";

    public static String NAMESPACE = "http://service.movil.abasto.neto";
    public static String METHOD_NAME_VALIDAUSUARIO = "validaUsuario";
    public static String NAMESPACEVALIDAUSUARIO = "http://servicio.pickeo.movil.abasto.neto";
    public static String METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR = "guardarArtsContadosVerificador";
    public static String METHOD_NAME_GETVALIDAPEDIDOVERIFICADOR = "getValidaPedidoVerificador";
    public static String METHOD_NAME_GUARDARDIFERENCIASVERIFICADO = "guardarDiferenciasVerificado";
    public static String METHOD_NAME_GETCATALOGOARTICULOSVERIFICADORGENERAL = "getCatalogoArticulosVerificadorGeneral";


    public static final char[] LLAVE_BKS = "p0JXRjVkbc06fyK".toCharArray();
    public static final String CLAVE_CIFRADO = "keyNetoCifrado";





    public static final String CADENA_CONEXION   = "http://10.81.12.46:7777/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica";

    //Pre QA
    //public static final String CADENA_CONEXION = "https://10.37.140.202:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica";
    //QA
    //public static final String CADENA_CONEXION = "https://10.81.12.46:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica";

    //DESARROLLO
    //public static final String CADENA_CONEXION = "https://10.81.12.45:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica";
    //Produccion pruebas
    //public static final String CADENA_CONEXION = "https://200.38.108.77/WSGenericosMovil/ssl/servicio/consultaGenericaDinamica";
    //Produccion
    //public static final String CADENA_CONEXION = "https://www.servicios.tiendasneto.com/WSGenericosMovil/ssl/servicio/consultaGenericaDinamica";




}
