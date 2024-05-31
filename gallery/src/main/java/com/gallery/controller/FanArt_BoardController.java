package com.gallery.controller;

import java.io.IOException;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FanArt_BoardController {
	
	@RequestMapping("/fanArt_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("fanArt_board/list");
		
		return model;
	}
}
