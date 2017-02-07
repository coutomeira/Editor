import java.util.ArrayList;
import java.util.List;

/**
 * BUFFER
 * <p>
 * A implementa&ccedil;&atilde;ao dos m&eacute;todos deste buffer pretende ser eficaz e simples.
 * <p>
 * Note-se que um buffer contem sempre uma linha l&oacute;gica, nem que seja uma linha com um espa&ccedil;o em branco.<br>
 * Deste modo garantimos que n&atilde;o acontecem erros com a chamada dos m&eacute;todos que fazem altera&ccedil;&otilde;es sobre os mesmos.<br>
 * <p>
 * Est&aacute; implementado o m&eacute;tdo insert() e delete() que permite modificar o buffer.<br>
 * Para al&eacute;m disso, as fun&ccedil;&otilde;es move_() que alteram as posi&ccedil;&otilde;es do cursor.<br>
 * H&aacute; ainda os m&eacute;todos que permitem obter informa&ccedil;&atilde;o sobre o buffer. Iniciam-se com o prefixo get.
 *
 *@author Andr&eacute; Meira
 *@author Joel Sousa
 */
public class Buffer {

    private List<StringBuilder> buff;
    private int cursorRow;
    private int cursorCol;


    /**
     * Constr&oacute;i um buffer vazio. Inicialmente o cursor &eacute; colocado na linha zero e coluna zero.
     */
    public Buffer() {
        buff = new ArrayList<StringBuilder>();

        StringBuilder str = new StringBuilder();

        str.append("");

        buff.add(str);
        this.cursorRow = 0;
        this.cursorCol = 0;
    }

    /**
     * Retorna a linha onde se encontra o cursor.
     *
     * @return A linha onde se encontra o cursor.
     */
    public int getRow() {
        return cursorRow;
    }

    /**
     * Retorna a coluna onde se encontra o cursor.
     *
     * @return A coluna onde se encontra o cursor.
     */
    public int getCol() {
        return cursorCol;
    }

    /**
     * Retorna a quantidade de linhas l&oacute;gicas do buffer.
     *
     * @return A quantidade de linhas l&oacute;gicas do buffer.
     */
    public int getNumLines() {
        return buff.size();
    }


    /**
     * Retorna uma frase correspondente a uma linha l&oacute;gica.
     *
     * @param a Ind&iacute;ce da frase pretendida.
     * @return A frase pretendida.
     * @throws IndexOutOfBoundsException Se o &iacute;ndice &eacute; maior do que a quantida de linhas deste buffer.
     */
    public String getString(int a)
    {
        try
        {
            return buff.get(a).toString();
        }

        catch (IndexOutOfBoundsException e)
        {
            throw new IndexOutOfBoundsException("Index " + a + " is out of bounds!");
        }
    }

    /**
     * Retorna todo o conjunto de frases existentes que constituem o buffer.
     *
     * @return O conjunto de frases que constituem o buffer.
     */
    public ArrayList<StringBuilder> getAllLines()
    {
        return (ArrayList<StringBuilder>) buff;
    }

    /**
     *Indica se h&aacute; alguma informa&ccedil;&atilde;o presente no cursor. Isto &eacute;, se n&atilde; estamos na presen&ccedil;a de apenas linhas em branco.
     *
     * @return Verdade se houver pelo menos um caracter.
     */
    public boolean noInforamtion()
    {
        for (StringBuilder s : getAllLines())
        {
            if(!s.toString().trim().isEmpty())
                return false;
        }
        return true;
    }
    /**
     * Apaga o caracter que se encontra imediatamente antes da posi&ccedil;&atilde;o do cursor.
     *<p>
     * Formalmente, quando o cursor se encontra numa coluna diferente de zero, o elemento a apagar est&aacute; na mesma linha do cursor e na coluna anterior.<br>
     * Caso contr&aacute;rio, a linha l&aacute;gica onde o cursor se encontra &eacute; concatenada com a anterior. A linha do cursor passa a ser menos uma do que a atual e a coluna e o tamanho da linha anterior antes da concaten&ccedil;&atilde;o.<br>
     * Quando o cursor se encontra na linha zero e na coluna zero a chamada a este m&eacute;todo n&atilde;o produz qualquer altera&ccedil;&atilde;o.
     */
    public void delete()
    {
        // COLUNA > 0 && LINHA QUALQUER
        if(cursorCol > 0)
        {
            StringBuilder line = buff.get(cursorRow);

            line.deleteCharAt(cursorCol-1);

            cursorCol--;

            buff.set(cursorRow, line);
        }

        //COLUNA = 0 && LINHA > 0
        else if(cursorRow > 0)
        {
            StringBuilder line_down = buff.remove(cursorRow);

            cursorRow --;

            StringBuilder line_up = buff.get(cursorRow);

            cursorCol = line_up.length();

            line_up.append(line_down);

            buff.set(cursorRow, line_up);
        }

        //COLUNA = 0 && LINHA = 0
        //DO NOTHING
    }

    /**
     * Insere a frase especificada neste buffer no local onde se encontra o cursor.<br>
     * A frase recebida &eacute; analisada caracter a caracter.
     * <p>
     * Quando se trata de uma mudan&ccedil;a de linha, uma nova linha l&oacute;gica &eacute; criada contendo os elementos que se encontram na mesma linha do cursor mas &agrave; frente da coluna. O cursor passa para a linha seguinte e para a coluna zero.<br>
     * No caso de ser um outro caracter qualquer, &eacute; adicionado &agrave; linha l&oacute;gica onde se encontra o cursor. Assim, a linha deste mantem-se e passa para a coluna seguinte.
     * @param string Frase para inserir no buffer.
     */
    public void insert(String string)
    {

        for (int i = 0; i < string.length(); i++)
        {
           if(string.charAt(i) == '\n')
                insertLn();
           else if(string.charAt(i) == '\t')
               for(int j=0 ; j<4 ; j++)
                   insertChar(' ');
           else
               insertChar(string.charAt(i));

        }
    }


    private void insertLn()
    {
        String line = buff.get(cursorRow).substring(cursorCol); //OBTER SUBSTRING DA COLUNA ATE AO FIM

        StringBuilder new_line = new StringBuilder();

        new_line.append(line);

        buff.get(cursorRow).setLength(cursorCol); //ATUALIZAR O TAMANHO

        cursorRow++;
        cursorCol = 0;

        buff.add(cursorRow, new_line);
    }


    private void insertChar(char a)
    {
        StringBuilder line = buff.get(cursorRow);

        line.insert(cursorCol, a);

        buff.set(cursorRow, line);

        cursorCol++;
    }

    /**
     * Move o cursor para a posi&ccedil;&atilde;o especificada.
     * <p>
     * Quando a posi&ccedil;&atilde;o existe, o cursor &eacute; movido exatamente para o local prentendido.
     * Caso n&atilde;o exista, acontece uma de duas coisas. <br>
     * Se a quantidade de linhas l&oacute;gicas deste buffer &eacute; menor do que a linha pretendida, o cursor &eacute; colocado na &uacute;ltima coluna da &uacute;ltima linha.<br>
     * Quando isso n&atilde;o acontece compara-se a quantidade de colunas da linha l&oacute;gica para onde o cursor &eacute; movido com a coluna pretendida.
     * Se essa coluna ainda n&atilde;o estiver definida ent&atilde;o o cursor &eacute; colocado na &uacute;ltima coluna dessa mesma linha.
     *
     * @param x Linha pretendida.
     * @param y Coluna pretendida.
     */
    public void  move(int x, int y)
    {
        if (x < buff.size())
        {
            if (y <= buff.get(x).length())
            {
                cursorRow = x;
                cursorCol = y;
            }
            else
            {
                cursorRow = x;
                cursorCol = buff.get(x).length();
            }
        }
        else
        {
            cursorRow = buff.size()-1;
            cursorCol = buff.get(buff.size()-1).length();
        }
    }

    /**
     * O cursor &eacute; movido para a esquerda.
     * <p>
     * Formalmente, quando o cursor se encontra numa coluna diferente de zero, o cursor &eacute; colocado na coluna anterior mantendo-se na mesma linha.<br>
     * Caso contr&aacute;rio, passa para a linha l&oacute;gica anterior e para a &uacute;ltima coluna da mesma.<br>
     * Quando o cursor se encontra na linha zero e na coluna zero a chamada a este m&eacute;todo n&atilde;o produz qualquer altera&ccedil;&atilde;o.
     */
    public void moveLeft()
    {
        // COLUNA > 0 && LINHA QUALQUER
        if(cursorCol > 0)
        {
            cursorCol--;
        }

        //COLUNA = 0 && LINHA > 0
        else if(cursorRow > 0)
        {
            cursorRow--;
            cursorCol = buff.get(cursorRow).length();
        }

        //COLUNA = 0 && LINHA = 0
        //DO NOTHING
    }

    /**
     * O cursor &eacute; movido para a direita.
     * <p>
     * Formalmente, quando o cursor n&atilde;o se encontra na &uacute;ltima coluna &eacute; colocado na coluna seguinte mantendo-se na mesma linha.<br>
     * Caso contr&aacute;rio passa para a linha seguinte, caso exista, e para a coluna zero. Caso n&atilde;o exista nada &eacute; feito.<br>
     *
     */
    public void moveRight()
    {
        // SE NAO ESTA NA ULTIMA POSICAO DA LINHA
        if (cursorCol < buff.get(cursorRow).length())
            cursorCol++;

        // SE ESTA NA ULTIMA POSICAO MAS HA LINHAS ABAIXO
        else if(cursorRow < buff.size() - 1)
        {
            cursorCol = 0;
            cursorRow++ ;
        }

    }

    /**
     * O cursor &eacute; movido para cima.
     * <p>
     * Quando o cursor se encontra na primeira linha nada acontece.<br>
     * Caso contr&aacute;rio o cursor passa para a linha anterior. Se a linha anterior tem pelo menos o mesmo tamanho da atual, a coluna mantem-se. Se n&atilde;o, o cursor &eacute; movido para a &uacute;ltima coluna da linha l&oacute;gica anterior.
     */
    public void moveUp()
    {
        //SE NAO ESTOU NA PRIMEIRA LINHA
        if( cursorRow > 0)
        {
            cursorRow--;
            //SE ESTA LINHA E MENOR O CURSOR FICA NO FIM
            if(buff.get(cursorRow).length() < cursorCol)
                cursorCol = buff.get(cursorRow).length();
        }
    }

    /**
     * O cursor &eacute; movido para baixo.
     *  <p>
     * Quando o cursor se encontra na &uacute;ltima linha nada acontece.<br>
     * Caso contr&aacute;rio o cursor passa para a linha seguinte. Se a linha seguinte tem pelo menos o mesmo tamanho da atual, a coluna mantem-se. Se n&atilde;o, o cursor &eacute; movido para a &uacute;ltima coluna da linha l&oacute;gica seguinte.
     */
    public void moveDown() {
        //SE NAO ESTOU NA ULTIMA
        if (cursorRow < buff.size() - 1) {
            cursorRow++;
            //SE ESTA LINHA E MENOR O CURSOR FICA NO FIM
            if (buff.get(cursorRow).length() < cursorCol)
                cursorCol = buff.get(cursorRow).length();
        }
    }


}

