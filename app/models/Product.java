package models;


import play.data.validation.Constraints;
import play.libs.F;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;

import play.db.ebean.Model;

import javax.persistence.*;

import java.util.*;
import utils.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.validation.*;
import javax.validation.metadata.*;


@Entity
public class Product implements PathBindable<Product>,
    QueryStringBindable<Product> {

  private static List<Product> products;

  @Target({FIELD})
  @Retention(RUNTIME)
  @Constraint(validatedBy = EanValidator.class)
  @play.data.Form.Display(name="constraint.ean", attributes={"value"})
  public static @interface EAN {
      String message() default EanValidator.message;
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
  }

  public static class EanValidator extends Constraints.Validator<String> implements ConstraintValidator<EAN, String> {
    final static public String message = "error.invalid.ean";
        
    public EanValidator() {}

    @Override
    public void initialize(EAN constraintAnnotation) {}
    
    @Override
    public boolean isValid(String value) {
      String pattern = "^[0-9]{13}$";
      return value != null && value.matches(pattern);
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
      return new F.Tuple<String, Object[]>(message,
          new Object[]{});
    }
  }


  static {
    products = new ArrayList<Product>();
    products.add(new Product("1111111111111", "Paperclips 1",
        "Paperclips description 1"));
    products.add(new Product("2222222222222", "Paperclips 2",
        "Paperclips description "));
    products.add(new Product("3333333333333", "Paperclips 3",
        "Paperclips description 3"));
    products.add(new Product("4444444444444", "Paperclips 4",
        "Paperclips description 4"));
    products.add(new Product("5555555555555", "Paperclips 5",
        "Paperclips description 5"));
  }

  @Id
  public Long id;
  @Constraints.Required
  @EAN
  public String ean;
  @Constraints.Required
  public String name;
  public String description;
  public Date date = new Date();
  @DateFormat("yyyy-MM-dd")
  public Date peremptionDate = new Date();
  public byte[] picture;

  @ManyToMany
  public List<Tag> tags = new LinkedList<Tag>();

  @OneToMany(mappedBy = "product")
  public List<StockItem> stockItems;

  public Product() {
    // Left empty
  }

  public Product(String ean, String name, String description) {
    this.ean = ean;
    this.name = name;
    this.description = description;
  }

  public String toString() {
    return String.format("%s - %s", ean, name);
  }

  public static List<Product> findAll() {
    return new ArrayList<Product>(products);
  }

  public static Product findByEan(String ean) {
    for (Product candidate : products) {
      if (candidate.ean.equals(ean)) {
        return candidate;
      }
    }
    return null;
  }

  public static List<Product> findByName(String term) {
    final List<Product> results = new ArrayList<Product>();
    for (Product candidate : products) {
      if (candidate.name.toLowerCase().contains(term.toLowerCase())) {
        results.add(candidate);
      }
    }

    return results;
  }

  public static boolean remove(Product product) {
    return products.remove(product);
  }

  public void save() {
    products.remove(findByEan(this.ean));
    products.add(this);
  }

  @Override
  public Product bind(String key, String value) {
    return findByEan(value);
  }

  @Override
  public F.Option<Product> bind(String key, Map<String, String[]> data) {
    return F.Option.Some(findByEan(data.get("ean")[0]));
  }

  @Override
  public String unbind(String s) {
    return this.ean;
  }

  @Override
  public String javascriptUnbind() {
    return this.ean;
  }
}
