package com.hswt.broihier.gdo;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by broihier on 4/14/18.
 */

public class Key {
    private int M = 214326;
    private int A = 1807;
    private int C = 45289;
    private byte[] key;
    private String TAG = "Key";

    public Key(int seed) {
        Log.d(TAG,"Original seed: "+seed);
        seed = seed % M;
        int size = seed % 7 + 5;
        key = new byte[size];
        int index = 0;
        byte[] seedBytes = ByteBuffer.allocate(4).putInt(seed).array();
        for (byte aByte : seedBytes) {
            key[index++] = aByte;
        }
        while (index < size) {
            seed = (A * seed + C) % M;
            Log.d(TAG,"new seed: " + seed);
            byte[] newSeedBytes = ByteBuffer.allocate(4).putInt(seed).array();
            key[index++] = newSeedBytes[3];
            String fullSeed = "";
            for (byte aByte : newSeedBytes) {
                fullSeed += " " + aByte;
            }
            Log.d(TAG,"new Seed: " + fullSeed);
        }
        Log.d(TAG,toString());
    }
    public String toString() {
        String fullArray = "";
        for (byte aByte : key) {
            fullArray += " " + aByte;
        }
        return fullArray;
    }

    public byte[] getKey() {
        return key;
    }

    public boolean unlock(byte[] testKey) {
        boolean result = true;
        //int testSeed = testKey[0] << 24 + ((testKey[1] << 16) & 0xff0000) + ((testKey[2] << 8) & 0xff00) + (testKey[3] & 0xff);
        int testSeed = (testKey[0] << 24) + ((testKey[1] << 16) & 0xff0000) + ((testKey[2] << 8) & 0xff00) + (testKey[3] & 0xff);
        Key comparisonKey = new Key(testSeed);
        return comparisonKey.equals(this);
    }

    public boolean equals(Object thing) {
      if (thing.getClass() == this.getClass()) {
          Key testThing = (Key) thing;
          if (this.getKey().length == testThing.getKey().length) {
              byte[] aKey = this.getKey();
              byte[] bKey = testThing.getKey();
              for (int index = 0; index < testThing.getKey().length; index++){
                  if (aKey[index] != bKey[index]) {
                      Log.d(TAG,"aKey doesn't match bKey");
                      return false;
                  }
              }
              return true;
          } else {
              Log.d(TAG,"Lengths do not match");
              return false;
          }
      } else {
          Log.d(TAG,"Class doesn't match");
          return false;
      }
    };

}
