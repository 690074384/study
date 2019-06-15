package com.lph.util.restful.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author lvpenghui
 * @since 2019-6-1 01:29:44
 */
@Getter
@Setter
@ToString
public class Depth {
	private List<String> bids;
	private List<String> asks;
}
