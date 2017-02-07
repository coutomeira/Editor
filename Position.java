
public class Position {


        private int line, col;

    /**
     * Construtor sem argumentos QUE inicia na linha e coluna zero.
     */
        Position(){
        line= 0;
        col = 0;
        }

    /**
     * COnstrutor com argumentos inicia na linha e coluna indicadas.
     * @param Row Linha pretendida
     * @param Col Coluna pretendida
     */
        Position(int Row, int Col){
            line= Row;
            col = Col;
        }

    /**
     *Obter a linha atual da posi&ccedil;&atilde;o
     *
     * @return A linha atual da posi&ccedil;&atilde;o
     */
        public int getLine() {
            return line;
        }

    /**
     *Actualiza a linha atual da posi&ccedil;&atilde;o
     *
     * @param line Nova linha da posi&ccedil;&atilde;o
     */
        public void setLine(int line) {
            this.line = line;
        }

    /**
     *Obter a coluna atual da posi&ccedil;&atilde;o
     *
     * @return A coluna atual da posi&ccedil;&atilde;o
     */
        public int getCol() {
            return col;
        }


    /**
     *Actualiza a coluna atual da posi&ccedil;&atilde;o
     *
     * @param col Nova coluna da posi&ccedil;&atilde;o
     */
        public void setCol(int col) {
            this.col = col;
        }


    }
