package fs;

import java.nio.ByteBuffer;

public class Head {

    public Head()
    {
        data = new byte[8];
    }

    public Head(byte[] _data)
    {
        if (_data == null || _data.length != 8)
            throw new Error("Head data needs to be 8 bytes long");
        data = _data;
    }

    public Head(Integer free, Integer occupied)
    {
        data = new byte[8];
        setFree(free);
        setOccupied(occupied);
    }

    void setFree(Integer free)
    {

        ByteBuffer.wrap(data).putInt(0, free);
    }

    void setOccupied(Integer occupied)
    {

        ByteBuffer.wrap(data).putInt(4, occupied);
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
