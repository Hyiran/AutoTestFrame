package com.atf.cap.dbdriver;


import com.atf.cap.dbdriver.environment.DatabaseDriverName;
import com.atf.cap.exception.CapDbAccessException;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author sqou
 *         2015/9/25
 */
public class DbInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbInitializer.class);

    private static final String DB_AIO_ITEM_SEPARATOR = ";";
    private static final String DB_AIO_VALUE_SEPARATOR = "=";
    private static final String DB_AIO_PORT_SEPARATOR = ",";

    private static final Map<String, JdbcConnectInfo> JDBC_CONNECT_INFO_MAP = new HashMap<String, JdbcConnectInfo>();

    private static HttpClient sslClient = initWeakSSLClient();


    /**
     * It will read the cap-db-conn.properties file from the root of classpath. If found, will
     * use the values inside it to initialize the database source.
     * <p/>
     * The properties win-strategy will be:
     * 1. Check db.connection.crypto, if provided, means in development mode. Just use it. Finish.
     * 2. Then check db.connection.testfarm and db.instance. Should provided both, then Finish. otherwise throw error.
     * 3. Next check db.driver.name, db.driver.url, db.driver.username, db.driver.password those 4 must provided in this step.
     * <p/>
     * For db.type and db.driver.name, must provided at least one. If both, the db.driver.name wins.
     *
     * @param inputStream
     * @return
     */
    public static JdbcConnectInfo initDataSource(InputStream inputStream) {

        try {
            Properties properties = new Properties();
            properties.load(inputStream);

            // initial logic
            String dbType = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_DBTYPE);
            String dbDriverName = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_DRIVERNAME);
            String dbDriverUrl = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_DRIVERURL);
            String dbUserName = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_USERNAME);
            String dbPassword = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_PASSWORD);
            String dbTestFarm = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_CONN_TESTFARM);
            String dbCryptoConn = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_CONN_CRYPTO);
            String dbInstance = properties.getProperty(CapDBConstants.DB_CONN_PROP_KEY_DBINSTANCE);

            // use the db all-in-one way, development mode, all needed info is provided
            if (!Strings.isNullOrEmpty(dbCryptoConn)) {
                LOGGER.debug("Use db all-in-one way, development mode.");
                String decryptConnStr = decryptConnString(dbCryptoConn);

                return initializeSQLServerConnection(decryptConnStr);
            }

            if (Strings.isNullOrEmpty(dbInstance) && !Strings.isNullOrEmpty(dbTestFarm)
                    || !Strings.isNullOrEmpty(dbInstance) && Strings.isNullOrEmpty(dbTestFarm)) {
                throw new CapDbAccessException("Error: Must specify both the DB all-in-one URL and Database name to be connected.");
            }

            // use the db all-in-one way, normal
            if (!Strings.isNullOrEmpty(dbInstance) && !Strings.isNullOrEmpty(dbTestFarm)) {
                LOGGER.debug("Use db all-in-one way, normal mode.");

                String cryptoConn = invokeAPIService(dbTestFarm, dbInstance);

                if (cryptoConn == null) {
                    throw new CapDbAccessException("Error: Not able to retrieve connection string from DB all-in-one service.");
                } else {
                    String decryptConnStr = decryptConnString(cryptoConn);

                    return initializeSQLServerConnection(decryptConnStr);
                }
            }

            // failover to raw jdbc config
            boolean meetPrecondition = !Strings.isNullOrEmpty(dbDriverUrl) && !Strings.isNullOrEmpty(dbUserName) && !Strings.isNullOrEmpty(dbPassword);
            if (meetPrecondition && (!Strings.isNullOrEmpty(dbType)) || !Strings.isNullOrEmpty(dbDriverName)) {
                LOGGER.debug("Use JDBC way, all needed info is provided in advance.");
                // use dbDriverName first
                if (!Strings.isNullOrEmpty(dbDriverName)) {
                    LOGGER.debug("Use JDBC way, user specify the driver name explicit.");

                    return initializeJDBCConnection(dbDriverName, dbDriverUrl, dbUserName, dbPassword);
                } else {
                    LOGGER.debug("Use JDBC way, user take the embedded driver name.");

                    return null;
                }
            } else {
                LOGGER.warn("Not all JDBC required connection information is provided. The DB access function may not able to work properly.");
                return null;
            }
        } catch (IOException e) {

            LOGGER.info("The default config file " + CapDBConstants.DB_CONN_PROP_KEY_DBTYPE + " is not provided.");
            return null;
        }
    }


    /**
     * Copy from framework team, DB All-in-One project.
     *
     * @param dataSource
     * @return
     */
    protected static String decryptConnString(String dataSource) {
        if (dataSource == null || dataSource.length() == 0) {
            return "";
        }
        byte[] sources = Base64.decodeBase64(dataSource);
        int dataLen = sources.length;
        int keyLen = (int) sources[0];
        int len = dataLen - keyLen - 1;
        byte[] datas = new byte[len];
        int offset = dataLen - 1;
        int i = 0;
        int j = 0;
        byte t;
        for (int o = 0; o < len; o++) {
            i = (i + 1) % keyLen;
            j = (j + sources[offset - i]) % keyLen;
            t = sources[offset - i];
            sources[offset - i] = sources[offset - j];
            sources[offset - j] = t;
            datas[o] = (byte) (sources[o + 1] ^ sources[offset - ((sources[offset - i] + sources[offset - j]) % keyLen)]);
        }
        return new String(datas);
    }


    /**
     * Use special way to force client accept request from all SSL server.
     *
     * @return
     */
    private static HttpClient initWeakSSLClient() {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            // do nothing, has been handled outside
        }
        b.setSslcontext(sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        b.setConnectionManager(connMgr);

        // finally, build the HttpClient;
        //      -- done!
        sslClient = b.build();

        return sslClient;
    }


    private static String invokeAPIService(String url, String dbname) {

        try {
            URI uri = new URIBuilder(url).addParameter("ids", dbname).build();

            if (sslClient != null) {
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(uri);

                HttpResponse response = sslClient.execute(httpGet);

                HttpEntity entity = response.getEntity();

                String content = EntityUtils.toString(entity);

                if ("200".equals(JsonPath.parse(content).read("$.status"))) {
                    return JsonPath.parse(content).read("$.data[0].connectionString");
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Not able to access DB all-in-one service", e);
        } catch (ClientProtocolException e) {
            LOGGER.error("Not able to access DB all-in-one service", e);
        } catch (IOException e) {
            LOGGER.error("Not able to access DB all-in-one service", e);
        }

        return null;
    }


    /**
     * DB All-in-One initialization style. From that we can only get those 5 separated info, latter has to
     * combine them into correct dbDriver and dbUrl.
     */
    protected static JdbcConnectInfo initializeSQLServerConnection(String decryptConnStr) {
        if (Strings.isNullOrEmpty(decryptConnStr)) {
            throw new CapDbAccessException("The decrypted connection string is empty or null");
        }

        JdbcConnectInfo connectInfo = new JdbcConnectInfo();

        Iterable<String> pairs = Splitter.on(DB_AIO_ITEM_SEPARATOR).trimResults().omitEmptyStrings().split(decryptConnStr);

        for (String pair : pairs) {
            String[] item = pair.split(DB_AIO_VALUE_SEPARATOR);
            String key = item[0].toLowerCase();
            String value = item[1];

            switch (key) {
                case JdbcConnectInfo.KEY_SERVER:
                    if (value.indexOf(DB_AIO_PORT_SEPARATOR) > 0) {
                        connectInfo.setDbServer(value.substring(0, value.indexOf(DB_AIO_PORT_SEPARATOR)));
                        connectInfo.setDbPort(Integer.parseInt(value.substring(value.indexOf(DB_AIO_PORT_SEPARATOR) + 1, value.length())));
                    } else {
                        connectInfo.setDbServer(value);
                    }
                    break;
                case JdbcConnectInfo.KEY_INSTANCE:
                    connectInfo.setDbInstance(value);
                    break;
                case JdbcConnectInfo.KEY_USERNAME:
                    connectInfo.setUsername(value);
                    break;
                case JdbcConnectInfo.KEY_PASSWORD:
                    connectInfo.setPassword(value);
                    break;
                default:
                    LOGGER.warn("Unrecognized configuration item" + key + ":" + value);
                    break;
            }
        }

        // use existing info to construct dbUrl and dbDriver
        connectInfo = normalizeJdbcInfo(connectInfo);

        LOGGER.debug("The JDBC connection Info is: " + connectInfo.toString());

        return connectInfo;
    }

    /**
     * Spring's JDBC initialization style. The dbUrl should already contains db server address
     * and database instance name.
     *
     * @param dbDriver
     * @param dbUrl
     * @param username
     * @param password
     */
    protected static JdbcConnectInfo initializeJDBCConnection(String dbDriver, String dbUrl, String username, String password) {
        JdbcConnectInfo connectInfo = new JdbcConnectInfo(dbDriver, dbUrl, username, password);

        LOGGER.debug("The JDBC connection Info is: " + connectInfo.toString());
        return connectInfo;
    }

    /**
     * From DB all-in-one we can only get DB Server's address, port, instance, username, password.
     * Need to check for connect info, like set the default port number, set the driver class name,
     * construct connection url from existing info.
     *
     * @param connectInfo
     * @return
     */
    private static JdbcConnectInfo normalizeJdbcInfo(JdbcConnectInfo connectInfo) {

        // use SQLServer driver name for DB all-in-one case
        if (connectInfo.getDbDriver() == null) {
            connectInfo.setDbDriver(DatabaseDriverName.SQLServer.getDriverName());
        }

        // use SQLServer default port if it's not specified
        if (connectInfo.getDbPort() == 0) {
            connectInfo.setDbPort(DatabaseDriverName.SQLServer.getDriverPort());
        }

        if (connectInfo.getDbUrl() == null) {
            connectInfo.setDbUrl("jdbc:sqlserver://" + connectInfo.getDbServer() + ":" + connectInfo.getDbPort()
                    + ";databaseName=" + connectInfo.getDbInstance());
        }

        return connectInfo;
    }

}
