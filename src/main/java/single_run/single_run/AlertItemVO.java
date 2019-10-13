/*
 * Project: erebus
 * 
 * File Created at 2017-9-16
 * 
 * Copyright 2016 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package single_run.single_run;

import lombok.Data;

import java.io.Serializable;

/**
 * @Type AlertItemVO.java
 * @Desc 
 * @author puchuncheng
 * @date 2017-9-16 下午12:32:12
 * @version 
 */
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
