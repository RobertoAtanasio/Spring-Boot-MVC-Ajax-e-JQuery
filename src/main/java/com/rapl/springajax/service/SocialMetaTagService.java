package com.rapl.springajax.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rapl.springajax.domain.SocialMetaTag;

// Classe para recuperação das informações via jsoup : https://jsoup.org/
// Atentar para verificar no site como está definido os campos obtidos no select abaixo. veja que 
// a linha: tag.setSite(doc.head().select("meta[name=twitter:site]").attr("content")); em getTwitterCardByUrl 
// o nome do site é diferente 
// da linha: tag.setSite(doc.head().select("meta[property=og:site_name]").attr("content"));

@Service
public class SocialMetaTagService {

	private static Logger log = LoggerFactory.getLogger(SocialMetaTagService.class);
	
	// Método para retornar a meta tag que tiver definida no site!
	
	public SocialMetaTag getSocialMetaTagByUrl(String url) {
		SocialMetaTag twitter = getTwitterCardByUrl(url);
		if (!isEmpty(twitter)) {
			return twitter;
		}
		
		SocialMetaTag openGraph = getOpenGraphByUrl(url);
		if (!isEmpty(openGraph)) {
			return openGraph;
		}
		
		return null;
	}
	
	private SocialMetaTag getTwitterCardByUrl (String url) {
		SocialMetaTag tag = new SocialMetaTag();
		try {
			Document doc = Jsoup.connect(url).get();
			tag.setTitle(doc.head().select("meta[name=twitter:title]").attr("content"));
			tag.setSite(doc.head().select("meta[name=twitter:site]").attr("content"));
			tag.setImage(doc.head().select("meta[name=twitter:image]").attr("content"));
			tag.setUrl(doc.head().select("meta[name=twitter:url]").attr("content"));
		} catch (IOException e) {
			log.error(">>> Error em SocialMetaTag getTwitterCardByUrl: " + e.getMessage(), e.getCause());
		}
		return tag;
	}
	
	private SocialMetaTag getOpenGraphByUrl (String url) {
		SocialMetaTag tag = new SocialMetaTag();
		try {
			Document doc = Jsoup.connect(url).get();
			tag.setTitle(doc.head().select("meta[property=og:title]").attr("content"));
			tag.setSite(doc.head().select("meta[property=og:site_name]").attr("content"));
			tag.setImage(doc.head().select("meta[property=og:image]").attr("content"));
			tag.setUrl(doc.head().select("meta[property=og:url]").attr("content"));
		} catch (IOException e) {
			log.error(">>> Error em SocialMetaTag getOpenGraphByUrl: " + e.getMessage(), e.getCause());
		}
		return tag;
	}
	
	private boolean isEmpty(SocialMetaTag tag) {
		if (tag.getImage().isEmpty()) return true;
		if (tag.getSite().isEmpty()) return true;
		if (tag.getTitle().isEmpty()) return true;
		if (tag.getUrl().isEmpty()) return true;		
		return false;
	}
}
