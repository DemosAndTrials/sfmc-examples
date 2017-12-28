package sfmc.repository;

import com.exacttarget.fuelsdk.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Fuel SDK Repository
 * https://developer.salesforce.com/docs/atlas.en-us.noversion.mc-sdks.meta/mc-sdks/index-sdk.htm
 * http://salesforce-marketingcloud.github.io/FuelSDK-Java/index.html?overview-summary.html
 * http://salesforce-marketingcloud.github.io/FuelSDK-Java/
 */
@Repository
public class FuelSDKRepository {

    private ETClient client;

//    public FuelSDKRepository() {
//        initSDKClient();
//    }

    /**
     * Instantiates an sdk client
     */
    private void initSDKClient() {
        ETConfiguration configuration = new ETConfiguration();
        // get config from heroku
        configuration.set("clientId", System.getenv("CLIENT_ID"));
        configuration.set("clientSecret", System.getenv("CLIENT_SECRET"));

        try {
            client = new ETClient(configuration);
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if client initiated
     */
    private void ensureClientInitialization() {
        if (client == null)
            initSDKClient();
    }

    /**
     * Gets access token
     */
    public void getToken() {
        String token = client.getAccessToken();
        System.out.println("*** TOKEN: " + token);
    }

    /**
     * Gets list of data extensions
     */
    public List<ETDataExtension> getDataExtensionsDetails() {
        try {
            ensureClientInitialization();
            List<ETDataExtension> exts = new ArrayList<>();
            ETResponse<ETDataExtension> response = client.retrieve(ETDataExtension.class);
            for (ETDataExtension ext : response.getObjects()) {
                exts.add(ext);
                System.out.println("DE: " + ext);
//                List<ETDataExtensionColumn> columns = ext.retrieveColumns();
//                for (ETDataExtensionColumn col : columns)
//                    System.out.println(col);
            }
            return exts;
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get data extension by id
     *
     * @param id
     * @return
     */
    public ETDataExtension getDataExtensionDetails(String id) {

        ensureClientInitialization();
        try {
            ETExpression expression = new ETExpression();
            expression.setProperty("id");
            expression.setOperator(ETExpression.Operator.EQUALS);
            expression.setValue(id);

            ETFilter filter = new ETFilter();
            filter.setExpression(expression);

            ETResponse<ETDataExtension> response = client.retrieve(ETDataExtension.class, filter);
            if (response.getObjects().size() > 0) {
                ETDataExtension ext = response.getObjects().get(0);
                ext.retrieveColumns();
                ext.getColumns().sort(Comparator.comparing(o -> o.getCreatedDate()));
                return ext;
            }
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ETDataExtensionRow getDataExtensionRowByEmail(String deKey, String email) {
        ETExpression expression = new ETExpression();
        expression.setProperty("Email");
        expression.setOperator(ETExpression.Operator.EQUALS);
        expression.setValue(email);

        ETFilter filter = new ETFilter();
        filter.setExpression(expression);

        List<ETDataExtensionRow> res = getDataExtensionRecords("key=" + deKey, filter);
        return res.size() > 0 ? res.get(0) : null;
    }

    /**
     * Get Row
     * @param key
     * @param today
     * @return
     * @throws ETSdkException
     */
    public ETDataExtensionRow getDataExtensionRecord(String key, String today) throws ETSdkException {
        // create expression
        // property on left side, value on right side
        ETExpression ex1 = ETExpression.parse("StartDate <= '" + today + "'");
        ETExpression ex2 = ETExpression.parse("EndDate >= '" + today + "'");
        ETExpression exp = new ETExpression();
        exp.addSubexpression(ex1);
        exp.setOperator(ETExpression.Operator.AND);
        exp.addSubexpression(ex2);
        // set filter
        ETFilter filter = new ETFilter();
        filter.setExpression(exp);
        // get record
        List<ETDataExtensionRow> res = getDataExtensionRecords("key=" + key, filter);
        return res.size() > 0 ? res.get(0) : null;
    }

    /**
     * Get records by DE Key
     *
     * @param key
     */
    public List<ETDataExtensionRow> getDataExtensionRecordsByKey(String key) {
        return getDataExtensionRecords("key=" + key, new ETFilter());
    }

    /**
     * Select DE records using filter
     *
     * @param dataExtension
     * @param filter
     * @return
     */
    private List<ETDataExtensionRow> getDataExtensionRecords(String dataExtension, ETFilter filter) {
        ensureClientInitialization();
        List<ETDataExtensionRow> records = new ArrayList<>();
        try {
            ETResponse<ETDataExtensionRow> res = ETDataExtension.select(client, dataExtension, filter);
            for (ETDataExtensionRow row : res.getObjects()) {
                System.out.println(row);
                records.add(row);
            }
            return records;
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * create DE Row
     *
     * @param record
     * @return
     */
    public ETDataExtensionRow createDataExtensionRow(ETDataExtensionRow record) {
        ensureClientInitialization();
        try {
            ETResponse<ETDataExtensionRow> res = client.create(record);// TODO use de.insert?!
            return res.getObject();
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * delete DE Row
     *
     * @param de
     * @param record
     * @return
     */
    public boolean deleteDataExtensionRow(ETDataExtension de, ETDataExtensionRow record) {
        try {
            ETResponse<ETDataExtensionRow> res = de.delete(record);
            if (res.getStatus() == ETResult.Status.OK)
                return true;
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * update DE Row
     *
     * @param de
     * @param record
     * @return
     */
    public ETDataExtensionRow updateDataExtensionRow(ETDataExtension de, ETDataExtensionRow record) {
        ensureClientInitialization();
        try {
            ETResponse<ETDataExtensionRow> res = de.update(record);
            if (res.getStatus() == ETResult.Status.OK)
                return res.getObject();
            return null; // TODO throw exception
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update DE row
     * row should include de key
     * @param record
     * @return
     */
    public ETDataExtensionRow updateDataExtensionRow(ETDataExtensionRow record) {

        try {
            ETResponse<ETDataExtensionRow> res = ETDataExtensionRow.update(client, Arrays.asList(record));
            if (res.getStatus() == ETResult.Status.OK)
                return res.getObject();
            return null; // TODO throw exception
        } catch (ETSdkException e) {
            e.printStackTrace();
        }
        return null;
    }
}
