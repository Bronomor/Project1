package Elements;

public interface IGenotypeConverter {
    default String genotypeToString(short[] genotype){
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i<32; i++) tmp.append(genotype[i]);
        return tmp.toString();
    }
}
