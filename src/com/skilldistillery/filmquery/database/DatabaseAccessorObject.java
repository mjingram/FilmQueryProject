package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";
	private String user = "student";
	private String pass = "student";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

  @Override
  public Film findFilmById(int filmId) {
    Film film = null;
    try {
    	
    	Connection conn = DriverManager.getConnection(URL, user, pass);
    	String sql = "SELECT film.id, title, description, release_year, language_id, rental_duration, "
        + " rental_rate, length, replacement_cost, rating, special_features, language.name "
       +  " FROM film JOIN language ON film.language_id = language.id WHERE film.id = ?";
    	PreparedStatement stmt = conn.prepareStatement(sql);
    	stmt.setInt(1, filmId);
    	ResultSet filmResult = stmt.executeQuery();
    	if(filmResult.next()){
    		film = new Film();
    		film.setFilmId(filmResult.getInt("film.id"));
    		film.setTitle(filmResult.getString("title"));
    		film.setDesc(filmResult.getString("description"));
    		film.setReleaseYear(filmResult.getShort("release_year"));
    		film.setLangId(filmResult.getInt("language_id"));
    		film.setRentDur(filmResult.getInt("rental_duration"));
    		film.setRate(filmResult.getDouble("rental_rate"));
    		film.setLength(filmResult.getInt("length"));
    		film.setRepCost(filmResult.getDouble("replacement_cost"));
    		film.setRating(filmResult.getString("rating"));
    		film.setFeatures(filmResult.getString("special_features"));
    		film.setLanguage(filmResult.getString("language.name"));
    		film.setActors(findActorsByFilmId(filmId, conn));
    	}
    }catch(SQLException e) {
    	e.printStackTrace();
    }  
	  return film;
  }
  
  @Override
  public List<Film> findFilmByKeyword(String keyword) {
    List<Film> films = new ArrayList<>();
    List<Actor> actors = new ArrayList<>();
    try {
    	
    	Connection conn = DriverManager.getConnection(URL, user, pass);
    	keyword = "%" + keyword + "%";
    	String sql = "SELECT film.id, title, description, release_year, language_id, rental_duration, "
        + " rental_rate, length, replacement_cost, rating, special_features, language.name "
       +  " FROM film JOIN language ON film.language_id = language.id WHERE title LIKE ? OR description LIKE ?";
    	PreparedStatement stmt = conn.prepareStatement(sql);
    	stmt.setString(1, keyword);
    	stmt.setString(2, keyword);
    	ResultSet filmResult = stmt.executeQuery();
    	while(filmResult.next()){
    		int FilmId = filmResult.getInt("film.id");
    		String Title = filmResult.getString("title");
    		String Desc = filmResult.getString("description");
    		short ReleaseYear = filmResult.getShort("release_year");
    		int LangId = filmResult.getInt("language_id");
    		int RentDur = filmResult.getInt("rental_duration");
    		double Rate = filmResult.getDouble("rental_rate");
    		int Length = filmResult.getInt("length");
    		double RepCost = filmResult.getDouble("replacement_cost");
    		String Rating = filmResult.getString("rating");
    		String Features = filmResult.getString("special_features");
    		String Language = filmResult.getString("language.name");
    		actors = findActorsByFilmId(FilmId, conn);
    		Film film = new Film(FilmId, Title, Desc, ReleaseYear, LangId, RentDur, Rate,
    				Length, RepCost, Rating, Features, Language, actors);
    		films.add(film);
    	}
    }catch(SQLException e) {
    	e.printStackTrace();
    }  
	  return films;
  }

@Override
public Actor findActorById(int actorId) {
	Actor actor = null;
	try {
	Connection conn = DriverManager.getConnection(URL, user, pass);
	String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
	PreparedStatement stmt = conn.prepareStatement(sql);
	stmt.setInt(1, actorId);
	ResultSet actorResult = stmt.executeQuery();
	if(actorResult.next()) {
		actor = new Actor();
		actor.setId(actorResult.getInt(1));
		actor.setFirstName(actorResult.getString(2));
	    actor.setLastName(actorResult.getString(3));
	    
	}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	return actor;
	
}

@Override
public List<Actor> findActorsByFilmId(int filmId, Connection conn) {
	List<Actor> actors = new ArrayList<>();
	  try {

//	    Connection conn = DriverManager.getConnection(URL, user, pass);
	    String sql = "SELECT id, first_name, last_name FROM actor "
	    		+ "JOIN film_actor ON actor.id = film_actor.actor_id "
	               + " WHERE film_id = ?";
	    PreparedStatement stmt = conn.prepareStatement(sql);
	    stmt.setInt(1, filmId);
	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
	      int actorId = rs.getInt("id");
	      String firstName = rs.getString("first_name");
	      String lastName = rs.getString("last_name");
	      Actor actor = new Actor(actorId, firstName, lastName);
	      actors.add(actor);
	    }
	  } catch (SQLException e) {
	    e.printStackTrace();
	  }
	  return actors;
	}
	
}


