package fs;

import java.nio.ByteBuffer;

public class Head {

    public Head(byte[] _data)
    {
        if (data.length != 8)
            throw new Error("Head data needs to be 8 bytes long");
        _data = data;
    }

    public Head(Integer free, Integer occupied)
    {
        byte[] data = new byte[8];
        setFree(free);
        setOccupied(occupied);
    }

    void setFree(Integer free)
    {

        System.arraycopy(free.byteValue(), 0, data, 0, 4);
    }

    void setOccupied(Integer occupied)
    {

        System.arraycopy(occupied.byteValue(), 0, data, 4, 8);
    }

    Integer getFree()
    {
        return ByteBuffer.wrap(data).getInt(0);
    }

    Integer getOccupied()
    {

        return ByteBuffer.wrap(data).getInt(4);
    }

    public byte[] getData()
    {
        return data;
    }

    private byte[] data;
}
