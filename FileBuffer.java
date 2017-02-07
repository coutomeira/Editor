import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileBuffer extends Buffer {


    private String path = "Sem Nome Definido";


    /**
     * Atualiza o diret&oacute;rio para eventos futuros.
     *
     * @param textPath Indica o diret&oacute;rio.
     */
    public void setPath(String textPath) {
        this.path = textPath;
    }


    /**
     * Retorna o diret&oacute;rio usado em eventos futuros.
     *
     * @return O diret&oacute;rio.
     */
    public String getFileName() { return Paths.get(path).getFileName().toString(); }





    /**
     * Permite guardar  informa&ccedil;&atilde;o do buffer num diret&oacute;rio definido. S&oacute; acontece quando o diret&oacute;rio &eacute; v&aacute;lido.
     * Isto &eacute;, quando o ficheiro final n&atilde;o existe n&atilde;o h&aacute; qualquer tipo de problema pois um novo ficheiro ser&aacute; criado.
     * No entanto, quando o caminho n&atilde;o est&aacute; todo definido at&eacute; ao final &eacute; retornado erro.
     *
     * @return Um inteiro de acordo com o sucesso ou insucesso do evento
     */
    public int save() {

        try {
            boolean firstline = true;
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);


            for (int i = 0; i < getNumLines(); i++) {
                String phrase = getString(i);

                if (firstline) {
                    bw.write(phrase);
                    firstline = false;
                } else {
                    bw.newLine();
                    bw.write(phrase);
                }
            }
            bw.close();
        return 1;

        } catch (IOException e) {
            return 0;
        }
    }





    /**
     * Permite obter a informa&ccedil;&atilde;o de um ficheiro e pass&aacute;-la para o nosso buffer.
     * S&oacute; acontece quando um diret&oacute;rio &eacute; v&aacute;lido. Isto &eacute;, quando o ficheiro final existe e todo o caminho at&eacute; a si tamb&eacute;m.
     *
     * @return  Um inteiro de acordo com o sucesso ou insucesso do evento
     */
    public int open() {

        try {

            BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);

            int numLines = getNumLines();

            if(noInforamtion())
            {
                for(int i=0 ; i < numLines ; i++)
                    delete();
            }
            else {
                move(numLines, 0);
                insert("\n\n// O FICHEIRO QUE ABRIU //\n\n");
            }

            while (br.ready()) {
                String linha = br.readLine() + "\n";
                insert(linha);
            }
            br.close();

            return 1;
        }

         catch (IOException e) {
            return 0;
        }
    }


}