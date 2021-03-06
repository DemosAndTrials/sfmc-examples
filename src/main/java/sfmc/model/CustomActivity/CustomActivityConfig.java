package sfmc.model.CustomActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Custom Activity Config object
 * https://developer.salesforce.com/docs/atlas.en-us.noversion.mc-app-development.meta/mc-app-development/custom-activity-config.htm
 */
@Entity
public class CustomActivityConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer Id;

    @NotEmpty
    @Column(length = 100)
    String Key;

    @Column(length = 25)
    String Type;

    @NotEmpty
    @Column(length = 100)
    String Name;

    String Description;

    String SmallImageUrl;

    String BigImageUrl;

    Boolean IsConfigured;

    Boolean ConfigOnDrop;

    @NotEmpty
    String EditUrl;

    @NotNull
    Integer EditHeight;

    @NotNull
    Integer EditWidth;

    @NotEmpty
    String EndpointUrl;

    Boolean UseJwt;

    @ElementCollection
    @CollectionTable(name = "CustomActivityStep", joinColumns = {@JoinColumn(name = "config_id")})
    private List<CustomActivityStep> Steps = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "CustomActivitySplit", joinColumns = {@JoinColumn(name = "config_id")})
    private List<CustomActivitySplit> Splits = new ArrayList<>();

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSmallImageUrl() {
        return SmallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        SmallImageUrl = smallImageUrl;
    }

    public String getBigImageUrl() {
        return BigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        BigImageUrl = bigImageUrl;
    }

    public Boolean getConfigured() {
        return IsConfigured;
    }

    public void setConfigured(Boolean configured) {
        IsConfigured = configured;
    }

    public Boolean getConfigOnDrop() {
        return ConfigOnDrop;
    }

    public void setConfigOnDrop(Boolean configOnDrop) {
        this.ConfigOnDrop = configOnDrop;
    }

    public String getEditUrl() {
        return EditUrl;
    }

    public void setEditUrl(String editUrl) {
        EditUrl = editUrl;
    }

    public Integer getEditHeight() {
        return EditHeight;
    }

    public void setEditHeight(Integer editHeight) {
        EditHeight = editHeight;
    }

    public Integer getEditWidth() {
        return EditWidth;
    }

    public void setEditWidth(Integer editWidth) {
        EditWidth = editWidth;
    }

    public String getEndpointUrl() {
        return EndpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        EndpointUrl = endpointUrl;
    }

    public Boolean getUseJwt() {
        return UseJwt;
    }

    public void setUseJwt(Boolean useJwt) {
        this.UseJwt = useJwt;
    }

    public List<CustomActivityStep> getSteps() {
        return Steps;
    }

    public void setSteps(List<CustomActivityStep> steps) {
        Steps = steps;
    }

    public List<CustomActivitySplit> getSplits() {
        return Splits;
    }

    public void setSplits(List<CustomActivitySplit> splits) {
        Splits = splits;
    }

    public CustomActivityConfig() {
        init("");
    }

    public CustomActivityConfig(String host) {
        init(host);
    }

    private void init(String host) {
        this.setName("My Custom Activity");
        this.setKey(UUID.randomUUID().toString());
        this.setEditHeight(600);
        this.setEditWidth(800);
        this.Steps.add(new CustomActivityStep("Step 1", "step_1"));
        if (!host.isEmpty()) {
            this.EndpointUrl = host + "/ca";
            this.EditUrl = host + "/ca/ui";
            this.BigImageUrl = host + "/images/ca/icon.png";
            this.SmallImageUrl= host + "/images/ca/icon_small.png";
        }
    }

    @Override
    public String toString() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
