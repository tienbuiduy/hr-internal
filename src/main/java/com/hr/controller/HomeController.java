package com.hr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.constant.CommonConstant;
import com.hr.model.GoogleUser;
import com.hr.model.Role;
import com.hr.model.User;
import com.hr.service.FunctionService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
	@Autowired
	protected FunctionService functionService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected OAuth2AuthorizedClientService clientService;

	@RequestMapping("/")
	public RedirectView employees(Model model) {
		RedirectView rv = new RedirectView();
		rv.setContextRelative(true);
		rv.setUrl("/employees");
		return rv;
	}

	@RequestMapping("/employees")
	public String employees(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/employees"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/employee";
	}

	@RequestMapping("/customers")
	public String customers(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/customers"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/customer";
	}

	@RequestMapping("/opportunities")
	public String opportunity(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/opportunities"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/opportunity";
	}

	@RequestMapping("/allocations")
	public String allocations(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/allocations"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/allocations";
	}

	@RequestMapping("/projects")
	public String projects(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/projects"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/project";
	}

	@RequestMapping("/lessonlearns")
	public String lessonLearns(Model model, OAuth2AuthenticationToken authentication) throws JsonProcessingException {
		GoogleUser googleUser = getGoogleUser(authentication);
		model.addAttribute("googleUser", googleUser);
		model.addAttribute("is_role", checkRoleUser(googleUser, "/lessonlearns"));
		model.addAttribute("user_role", getUserRoleList(googleUser));
		return "layout/mainright/lessonlearns";
	}

	protected GoogleUser getGoogleUser(OAuth2AuthenticationToken authentication) {
		OAuth2AuthenticatedPrincipal oauthUser = authentication.getPrincipal();
		ObjectMapper objectMap = new ObjectMapper();
		GoogleUser googleUser = objectMap.convertValue(oauthUser.getAttributes(), GoogleUser.class);
		String googleUserName = googleUser.getName();
		int secondSpaceIndex = googleUserName.indexOf(" ", googleUserName.indexOf(" ") + 1);
		if(secondSpaceIndex < 15){
			googleUser.setName(googleUserName.substring(0, secondSpaceIndex));
		}
		else{
			googleUser.setName(googleUserName.substring(0, 15));
		}
		return googleUser;
	}

	protected boolean checkRoleUser(GoogleUser googleUser, String path) {
		User user = userService.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
		if (user != null) {
			return functionService.checkRoleUser(user.getId(), path);
		} else {
			return false;
		}
	}

	protected List<String> getUserRoleList(GoogleUser googleUser) {
		User user = userService.findByEmailAndIsDeleted(googleUser.getEmail(), CommonConstant.NOT_DELETED);
		List<String> roleList = new ArrayList<>();
			for(Role role: user.getRoles()){
				roleList.add(role.getRoleCode());
			}
			return roleList;

	}

	@GetMapping("/logoutSuccess")
	public RedirectView logoutModel(HttpServletRequest request, Model model, OAuth2AuthenticationToken oauthToken) throws JsonProcessingException {
		OAuth2AuthorizedClient client =
				clientService.loadAuthorizedClient(
						oauthToken.getAuthorizedClientRegistrationId(),
						oauthToken.getName());

		String accessToken = client.getAccessToken().getTokenValue();
		String data = executePost("https://accounts.google.com/o/oauth2/revoke?token=" + accessToken, "");
		RedirectView rv = new RedirectView();
		rv.setContextRelative(true);
		rv.setUrl("/employees");
		return rv;
	}

	public static String executePost(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			//Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
