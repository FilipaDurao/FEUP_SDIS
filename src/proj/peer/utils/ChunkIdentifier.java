package proj.peer.utils;

public class ChunkIdentifier {

    private Integer chunkNo;
    private byte[] body;

    public ChunkIdentifier(Integer chunkNo, byte[] body) {

        this.chunkNo = chunkNo;
        this.body = body;
    }

    public Integer getChunkNo() {
        return chunkNo;
    }

    public byte[] getBody() {
        return body;
    }
}
