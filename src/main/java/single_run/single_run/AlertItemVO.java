package single_run.single_run;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlertItemVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3485804313362958452L;

    private Long id;
    private Long monitorPageId;
    private String url;
    private String title;
    private Integer level;
    private String type;
    private Integer alertStatus;
    private String imagePath;
    private String imageUrlOrign;
    private String imageUrl;
    private Integer source;
    private String createdBy;
    private String createTime;
    private String groupName;
    private String responsiblePerson;// 负责人
    private String responsiblePhone;// 负责人联系电话
}

/**
 * Revision history
 * -------------------------------------------------------------------------
 * 
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2017-9-16 puchuncheng creat
 */
