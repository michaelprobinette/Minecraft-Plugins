/**
 * 
 */
package com.Vandolis.CodeRedLite;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Vandolis
 */
public class StringComp
{
  public static List<String> findMatches(List<String> possible, String str)
  {
    List<String> marked = new ArrayList<String>();
    Pattern p = Pattern.compile("[\\s]");
    String[] split = p.split(str);
    boolean add = false;
    for (int i = 1; (possible.size() > 1) && (i < (str.length() + 1)); i++)
    {
      marked.clear();
      for (String iter : possible)
      {
        add = false;
        for (String s : split)
        {
          if (i <= s.length())
          {
            if (!containsPermutations(iter.toLowerCase(), s.toLowerCase(), i))
            {
              //              System.out.println("String: " + iter + " is gone.");
              add = true;
              break;
            }
          }
        }
        if (add)
        {
          marked.add(iter);
        }
      }
      for (String iter : marked)
      {
        if ((possible.size() > 1) || (i == 1))
        {
          possible.remove(iter);
        }
        else
        {
          break;
        }
      }
    }
    return possible;
  }
  
  public static String smartPick(List<String> list, String str)
  {
    if (list.size() == 0)
    {
      //      System.out.println("Smart pick null");
      return null;
    }
    //    System.out.println("Possible: " + list.toString());
    String smallest = list.get(0);
    for (String iter : list)
    {
      if (iter.length() < smallest.length())
      {
        smallest = iter;
      }
    }
    //    System.out.println("Choose: " + smallest);
    return smallest;
  }
  
  private static boolean containsPermutations(String possible, String str, int toMatch)
  {
    if (toMatch > str.length())
    {
      throw new RuntimeException("toMatch is too large: " + toMatch);
    }
    if (toMatch > possible.length())
    {
      //      System.out.println("Possible is too small: " + possible + " " + toMatch);
      return false;
    }
    for (int i = 0; i <= (str.length() - toMatch); i++)
    {
      //      String debug = "Does \"" + possible + "\" contain \"" + str.subSequence(i, i + toMatch) + "\"";
      if (possible.contains(str.subSequence(i, i + toMatch)))
      {
        //        System.out.println(debug + "\tTrue");
        return true;
      }
      //      System.out.println(debug + "\tFalse");
    }
    return false;
  }
}
