/**
 *
 */
package com.vandolis.MineRift.Base;

/**
 * @author Vandolis
 */
public abstract class ClosingSequence implements Runnable
{
  private final boolean closeFast;
  private final Rift    rift;
  
  public ClosingSequence(Rift rift, boolean fast)
  {
    this.rift = rift;
    closeFast = fast;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    if (closeFast)
    {
      closeFast();
    }
    else
    {
      closeSlow();
    }
  }
  
  public abstract void closeFast();
  
  public abstract void closeSlow();
  
  /**
   * @return the rift
   */
  public Rift getRift()
  {
    return rift;
  }
}
