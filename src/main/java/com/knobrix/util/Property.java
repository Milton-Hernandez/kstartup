
package com.knobrix.util;

 /** Encapsulates a value that can be set only once.  A second attempt to
   * change the value will throw an Error.
   * @typeparam: T A Comparable payload
   * @copyright Epheriant,LLC, 2020
   * @author milton.
   */
public class Property<T extends Comparable> implements Comparable<T>  {
 //<editor-fold desc="Private parts..">
   protected boolean closed = false;
   protected T payload;
//</editor-fold>
 
/** Creates a sealed property.  (invocations to set will fail afterwards).
  * @param  arg: payload to be set. 
  */   
   public Property(T arg) {
         closed = true;
         payload = arg;
   }
   
/** Creates a yet-to-be-sealed property.  (invocations to set 
  * will fail afterwards).
  */   
   public Property() { }   
   
/** Sets the value of the Property payload
  * @param  arg: payload to be set. Can be invoked only once. Second attempt
  * will fail.
  */
   public synchronized void set(T arg) {
       if(closed)
           throw new Error("PROPS: Attempt to set a sealed value: " + arg);
       payload = arg;   
       closed = true;
   }
  
/** Gets the value of the Property PayLad
  * @returns Property payload.  Will return an Error if the payload hasn not 
  * been set.
  */   
   public synchronized T get() {
       if(!closed)
           throw new Error("PROPS: Attempt to get a non-set property");
       return payload;
   }
   
//<editor-fold desc="Object overrides..">
   @Override
   public int hashCode() {
       return get().hashCode();
   } 
   
   @Override
   public String toString() {
       return payload.toString();
   }
   
   
   public boolean equals(Property<T> other) {
       if(!closed || !other.closed)
           return false;
       var ret = payload.equals(other.payload);
       return ret;
   }
   
   @Override
   public boolean equals(Object other) {
      if(closed)
          return get().equals(other);
      return false;         
   }
   
   @Override
   public int compareTo(T other) {
      if(!closed)
          throw new Error("PROPS: Comparing unsealed instances");      
      return payload.compareTo(other);
   }
   
   public int compareTo(Property<T> other) {
      if(!closed)
          throw new Error("PROPS: Comparing unsealed instances");      
     return compareTo(other.payload);      
   }
   
//</editor-fold>
}

