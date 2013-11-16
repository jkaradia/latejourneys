package com.latejourneys.proxy;

import java.net.URL;


import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class OysterWebClient extends WebClient {

	private HtmlPage currentPage;

	public OysterWebClient() {
		 super(BrowserVersion.CHROME);

		super.setIncorrectnessListener(new IncorrectnessListenerImpl() {

			@Override
			public void notify(String arg0, Object arg1) {
				// System.err.println("Argument : " + arg0.toString() +
				// ", Object : ");
			}

		});
		
		final SilentCssErrorHandler eh = new SilentCssErrorHandler();
		super.setCssErrorHandler(eh);
		super.getOptions().setThrowExceptionOnScriptError(false);
		super.getOptions().setThrowExceptionOnFailingStatusCode(false);
		super.getOptions().setUseInsecureSSL(true);
		super.getOptions().setCssEnabled(false);
		super.getOptions().setPopupBlockerEnabled(false);
		super.getOptions().setRedirectEnabled(true);
		super.getOptions().setJavaScriptEnabled(true);
		super.setJavaScriptTimeout(3600);
		super.getOptions().setTimeout(9000);

		setCookieManager(new CookieManager() {
			protected int getPort(final URL url) {
				final int r = super.getPort(url);
				return r != -1 ? r : 80;
			}
		});  
	}

	
	
	public void setValue(HtmlForm form, String name, String value) {
		HtmlInput input = form.getInputByName(name);
		input.setValueAttribute(value);
	}

	public HtmlPage clickByName(HtmlForm form, String button) {
		HtmlInput submit = form.getInputByName(button);
		try {
			currentPage = submit.click();
			return currentPage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HtmlPage clickByValue(HtmlForm form, String button) {
		HtmlInput submit = form.getInputByValue(button);
		
		try {
			 submit.click();
			 waitForBackgroundJavaScript(20000);
			 currentPage = (HtmlPage) getCurrentWindow().getEnclosedPage();
			return currentPage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HtmlPage getPageByAnchor(HtmlPage page, String anchorName) {

		HtmlAnchor anchor = page.getAnchorByText(anchorName);
		try {
			 anchor.click();
			waitForBackgroundJavaScript(20000);
			 currentPage = (HtmlPage) getCurrentWindow().getEnclosedPage();
			return currentPage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public HtmlPage getPage(String s) {
		try {
			return super.getPage(s);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HtmlPage getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}

	public void select(HtmlForm form, String selection, String optionChosen) {
		HtmlSelect select = (HtmlSelect) form.getElementById(selection);

		HtmlOption option = select.getOptionByText(optionChosen);
		select.setSelectedAttribute(option, true);
	}

	public String getDefaultInputValue(HtmlPage page, String name) {
		HtmlInput input = (HtmlInput) page.getElementByName(name);
		return input.getValueAttribute();
	}

}
