package ma.shopflow.fidelite;

public enum NiveauFidelite {
    STANDARD(1),
    GOLD(2);

    private final int multiplicateur;

    NiveauFidelite(int multiplicateur) {
        this.multiplicateur = multiplicateur;
    }

    public int multiplicateur() {
        return multiplicateur;
    }
}
