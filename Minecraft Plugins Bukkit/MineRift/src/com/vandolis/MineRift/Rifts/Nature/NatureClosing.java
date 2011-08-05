/**
 *
 */
package com.vandolis.MineRift.Rifts.Nature;

import com.vandolis.MineRift.Base.ClosingSequence;
import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class NatureClosing extends ClosingSequence
{
  
  /**
   * @param rift
   * @param fast
   */
  public NatureClosing(Rift rift, boolean fast)
  {
    super(rift, fast);
    // TODO Auto-generated constructor stub
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.ClosingSequence#closeFast()
   */
  @Override
  public void closeFast()
  {
    getRift().restoreChunks();
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.ClosingSequence#closeSlow()
   */
  @Override
  public void closeSlow()
  {
    // TODO Auto-generated method stub
    
  }
  
}
