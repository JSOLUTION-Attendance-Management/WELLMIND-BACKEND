package site.wellmind.security.domain.vo;

public enum RequestStatus {
    Y,N,P;

    public static RequestStatus fromVerificationStatus(String status) {
        if ("approved".equalsIgnoreCase(status)) return Y;
        if ("pending".equalsIgnoreCase(status)) return P;
        return N;
    }
}
