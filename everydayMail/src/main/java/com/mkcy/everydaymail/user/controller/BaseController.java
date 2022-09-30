package com.mkcy.everydaymail.user.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/")
public class BaseController {
	@RequestMapping(value="/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> signUp(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);

		return map;
	}
}