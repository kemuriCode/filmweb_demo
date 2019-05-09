package rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.Comment;
import domain.Film;
import domain.Rating;

@Path("/film")
@Stateless
public class FilmResources {
	
	@PersistenceContext
	EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Film> getAll() {
		return em.createNamedQuery("film.all", Film.class).getResultList();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Film film) {
		em.persist(film);
		return Response.ok("added entity").build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		Film result = em.createNamedQuery("film.id", Film.class)
				.setParameter("filmId", id)
				.getSingleResult();
		if(result==null) {
			return Response.status(404).build();
		}
		return Response.ok(result).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") int id, Film f) {
		Film result = em.createNamedQuery("film.id", Film.class)
				.setParameter("filmId", id)
				.getSingleResult();
		if (result==null) {
			return Response.status(404).build();
		}
		result.setTitle(f.getTitle());
		result.setYear(f.getYear());
		result.setInfo(f.getInfo());
		em.persist(result);
		return Response.ok("updated entity").build();
		
	}
	
	@GET
	@Path("/{id}/comment")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getComment(@PathParam("id") int id) {
		Film result = em.createNamedQuery("film.id", Film.class)
							.setParameter("filmId", id)
							.getSingleResult();
		if(result==null) {
			return null;
		}
		return result.getComment();
	}
	
	@POST
	@Path("/{id}/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setComment(@PathParam("id") int id, Comment comment) {
		Film result = em.createNamedQuery("film.id", Film.class)
							.setParameter("filmId", id)
							.getSingleResult();
		if(result==null) {
			return Response.status(404).build();
		}
		result.getComment().add(comment);
		comment.setFilm(result);
		em.persist(comment);
		return Response.ok("added comment").build();
	}
	
	//usuwanie nie dziala
	@DELETE
	@Path("/{id}/comment")
	public Response deleteComment(@PathParam("id") int id) {
		
		Film film = em.createNamedQuery("film.id", Film.class)
							.setParameter("filmId", id)
							.getSingleResult();
		if(film==null) {
			return Response.status(404).build();
		}
		
		List<Comment> comment = em.createNamedQuery("comment.id", Comment.class)
								.getResultList();
		if(comment==null) {
			return Response.status(404).build();
		}
		
		for(Comment c : comment) {
			if(c.getFilm().getId()==film.getId()) {
				em.remove(c);
			}
		}
		return Response.ok("deleted comments").build();
	}
	
	@POST
	@Path("/{id}/rating")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setRating(@PathParam("id") int id, Rating rating) {
		Film result = em.createNamedQuery("film.id", Film.class)
							.setParameter("filmId", id)
							.getSingleResult();
		if(result==null) {
			return Response.status(404).build();
		}
		result.getRating().add(rating);
		rating.setFilm(result);
		em.persist(rating);
		return Response.ok("added rating").build();
	}
}
