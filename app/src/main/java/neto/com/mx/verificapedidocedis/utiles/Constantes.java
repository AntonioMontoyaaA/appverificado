package neto.com.mx.verificapedidocedis.utiles;

/**
 * Created by yruizm on 13/01/17.
 */

public class Constantes {

    public static final int AMBIENTE_DESARROLLO = 0, AMBIENTE_QA = 1, AMBIENTE_PROD = 2;

    private static final String[] URLS_LOGIN = new String[]{"http://10.81.12.45:7777/WSSIAN/services/WSPickeoMovil/",//DESA
            "http://10.81.12.46:7777/WSSIANPAR/services/WSPickeoMovil/", //QA
            "http://10.81.12.203:8003/WSSIONIndicadores/services/WSPickeoMovil/"}; //PROD
    private static final String[] URLS = new String[]{"http://10.81.12.45:7777/WSSIAN/services/WSVerificadorMovil/",//DESA
            "http://10.81.12.46:7777/WSSIANPAR/services/WSVerificadorMovil/",//QA
            "http://10.81.12.203:8003/WSSIONIndicadores/services/WSVerificadorMovil/"}; //PROD
    private static final String[] URLS_CIFRADAS = new String[]{"http://10.37.140.202:7777/WSSIANMoviles/services/WSAbastoMovil/", //DESA
            "http://10.81.12.46:7777/appWSSIANMovilesPAR/services/WSAbastoMovil/",//QA
            "https://200.38.108.77/WSSIANMoviles/services/WSAbastoMovil/"}; //PROD
    private static final String[] CADENAS_CONEXION = new String[]{"http://10.81.12.45:7777/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica/",//DESA
            "http://10.81.12.46:7777/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica/",//QA
            "http://10.81.12.203:8003/WSMovilREST/servicio/genericos/consultaGenericaDinamica/"/*, //PROD
            "https://www.servicios.tiendasneto.com/WSGenericosMovil/ssl/servicio/consultaGenericaDinamica"*/};  //PROD2

    public static final int AMBIENTE_APP = AMBIENTE_PROD;
    public  static final boolean IGNORE_APP_ID_CHECK = false;

    public static final String URL_STRING_LOGIN     = URLS_LOGIN[AMBIENTE_APP];
    public static final String URL_STRING           = URLS[AMBIENTE_APP];
    public static final String CADENA_CONEXION      = CADENAS_CONEXION[AMBIENTE_APP];

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



}
