/**
 *
 */
package com.vandolis.MineRift.Base;

/**
 * @author Vandolis
 */
public abstract class OpeningSequence implements Runnable
{
  private final boolean openFast;
  private final Rift    rift;
  
  public OpeningSequence(Rift rift, boolean fast)
  {
    this.rift = rift;
    openFast = fast;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    if (openFast)
    {
      openFast();
    }
    else
    {
      openSlow();
    }
  }
  
  public abstract void openFast();
  
  public abstract void openSlow();
  
  /**
   * @return the rift
   */
  public Rift getRift()
  {
    return rift;
  }
}
