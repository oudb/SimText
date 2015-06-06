

/**
 * Created by oudb on 2015/5/13.
 */
public class SimHash {
    public static final int  HASH_SIZE          = 64;
    public static MurmurHash hasher             = new MurmurHash();

    // byte gram
    private static final int FIXED_BGRAM_LENGTH = 8;


    /**
     *use short cuts to obtains a speed optimized simhash calculation
     * @param data
     * @param offset
     * @param length
     * @return
     */
    public static long computeOptimizedSimHashForBytes(byte[] data,int offset,int length) {

        LongOpenHashSet shingles = new LongOpenHashSet(Math.min(length/FIXED_BGRAM_LENGTH, 100000));

        for (int i = offset; i < length - FIXED_BGRAM_LENGTH + 1; i++) {
            int pos = i;
            // extract an ngram
            long shingle = data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos];

            shingles.add(shingle);
        }

        int v[] = new int[HASH_SIZE];
        byte longAsBytes[] = new byte[8];

        for (long shingle : shingles) {

            longAsBytes[0] = (byte) (shingle >> 56);
            longAsBytes[1] = (byte) (shingle >> 48);
            longAsBytes[2] = (byte) (shingle >> 40);
            longAsBytes[3] = (byte) (shingle >> 32);
            longAsBytes[4] = (byte) (shingle >> 24);
            longAsBytes[5] = (byte) (shingle >> 16);
            longAsBytes[6] = (byte) (shingle >> 8);
            longAsBytes[7] = (byte) (shingle);

            //long longHash = FPGenerator.std64.fp(longAsBytes, 0, 8);
            long hash1 = hasher.hash(longAsBytes, longAsBytes.length, 0);
            long hash2 = hasher.hash(longAsBytes, longAsBytes.length, (int)hash1);
            long longHash = (hash1 << 32) | hash2;
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }

        long simHash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simHash |= (1L << i);
            }
        }
        return simHash;
    }


    public static int hammingDistance(long hash1, long hash2) {
        long bits = hash1 ^ hash2;
        int count = 0;
        while (bits != 0) {
            bits &= bits - 1;
            ++count;
        }
        return count;
    }

    public static void main(String[] args) {
        String string1 = new String("asics 这两年在国内非常火，有赶超 nb的趋势了，文化底蕴决定一双鞋的基因");

        string1 = RegexUtil.trimSpecialSymbol(string1);
        System.out.println(string1);

        String string2 = new String("nike 这两年在国内非常火， nb的趋势了, 文化底蕴决定一双鞋的基因有赶超");

        string2 = RegexUtil.trimSpecialSymbol(string2);
        System.out.println(string2);

        byte[] data1 = string1.getBytes();
        byte[] data2 = string2.getBytes();

        long timeStart = System.currentTimeMillis();
        long simhash3 = computeOptimizedSimHashForBytes(data1,0,data1.length);
        long timeEnd = System.currentTimeMillis();
        System.out.println("New Calc for Document A Took:"
                        + (timeEnd - timeStart));

        timeStart = System.currentTimeMillis();
        long simhash4 = computeOptimizedSimHashForBytes(data2,0,data2.length);
        timeEnd = System.currentTimeMillis();
        System.out.println("New Calc for Document B Took:"
                     + (timeEnd - timeStart));

        int hammingDistance2 = hammingDistance(simhash3, simhash4);
         System.out.println("hammingdistance Doc (A) to Doc(B) NewWay:"
                        + hammingDistance2);
    }

}