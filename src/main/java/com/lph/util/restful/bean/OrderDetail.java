package com.lph.util.restful.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lvpenghui
 * @since 2019-5-24 20:11:27
 */
@Getter
@Setter
@ToString
public class OrderDetail {
    private String amount;

    private String price;

    private String coinname;

    private String id;

    private String time;

    private String type;
}
