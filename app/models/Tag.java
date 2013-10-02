package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Tag extends Model {

  public static List<Tag> tags = new LinkedList<Tag>();

  static {
    tags.add(new Tag(1L, "lightweight", Product.findByName("paperclips")));
    tags.add(new Tag(2L, "metal", Product.findByName("paperclips")));
    tags.add(new Tag(3L, "plastic", Product.findByName("paperclips")));
  }

  public static Tag findById(Long id) {
    for (Tag tag : tags) {
      if(tag.id == id) return tag;
    }
    return null;
  }

  @Id
  public Long id;
  public String name;

  @ManyToMany(mappedBy = "tags")
  public List<Product> products;


  public Tag(){
    // Left empty
  }

  public Tag(Long id, String name, Collection<Product> products) {
    this.id = id;
    this.name = name;
    this.products = new LinkedList<Product>(products);
    for (Product product : products) {
      product.tags.add(this);
    }
  }
}
