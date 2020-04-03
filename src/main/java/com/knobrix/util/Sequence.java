
package com.knobrix.util;

import java.util.HashMap;
import java.util.HashSet;

 /** Encapsulates a write-once sequence of objects.  It also allows to 
  *  search in reverse by index.
   * @typeparam: T A Comparable payload
   * @copyright Epheriant,LLC, 2020
   * @author milton.
   */
public class Sequence<T extends Comparable<T>> {
 //<editor-fold desc="Private parts..">
   protected boolean closed = false;
   protected T[] payload;
   protected HashMap<T,Integer> indexes;
   
   protected boolean hasDups(T[] arg)  {
       var temp = new HashSet<T>();
       for(int i=0; i<arg.length; i++)
           if(temp.contains(arg[i]))
               return true;
           else 
               temp.add(arg[i]);
       return false;
   }
   
   protected void build(T[] arg) {
      if(hasDups(arg))
          throw new Error("SEQ: Cannot create a Sequence with duplicates");
      indexes = new HashMap<T,Integer>();
      for(int i=0; i<arg.length;i++)
          indexes.put(arg[i],i);
      payload = arg;      
   }
   
//</editor-fold>
 
/** Creates a pre-sealed property.  (invocations to set will fail afterwards).
  * @param  arg: payload to be set. 
  */   
   public Sequence(T[] arg) {
         closed = true;
         build(arg);
   }
   
/** Creates a yet-to-be-sealed property.  (invocations to set 
  * will fail afterwards).
  */   
   public Sequence() { }   
   
/** Sets the value of the Property payload
  * @param  arg: payload to be set. Can be invoked only once. Second attempt
  * will fail.
  */
   public synchronized void set(T[] arg) {
       if(closed)
           throw new Error("SEQ: Attempt to set a sealed value: " + arg);  
       closed = true;
       build(arg);
   }
  
/** Gets the value of the Property PayLad
  * @returns Property payload.  Will return an Error if the payload hasn not 
  * been set.
  */   
   public synchronized T get(int idx) {
       if(!closed)
           throw new Error("SEQ: Attempt to get a non-set property");
       if(idx<0 || idx >= payload.length)
           throw new Error("SEQ: Index of our range");
       return payload[idx];
   }
   
/** Gets the value of the Property PayLad
  * @returns Property payload.  Will return an Error if the payload hasn not 
  * been set.
  */   
   public synchronized int locate(T arg) {
       if(!closed)
           throw new Error("SEQ: Attempt to get a non-set property");
       if(indexes.containsKey(arg))
           return indexes.get(arg);
       return -1;
   }   
   
  
}

