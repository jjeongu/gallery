package com.gallery.controller;

import java.io.IOException;
import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class GalleryController {
		@RequestMapping(value="/gallery/list", method=RequestMethod.GET)
		public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			ModelAndView mav = new ModelAndView("gallery/list");
			
			return mav;
		}
	}
