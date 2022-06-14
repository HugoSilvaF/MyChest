package com.github.hugosilvaf2.mychest.entity.chest;

public enum ChestSize {
    
   SMALLEST(9),
   SMALL(18),
   NORMAL(27),
   MEDIUM(36),
   BIG(45),
   BIGGEST(54);

   private int size;

   private ChestSize(int size) {
      this.size = size;
   }

   public int getSize() {
      return this.size;
   }
    
}
