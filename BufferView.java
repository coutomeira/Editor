
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.IOException;

import static com.googlecode.lanterna.TerminalFacade.createSwingTerminal;


public class BufferView {

    private Terminal terminal;
    private int startVisualRow = 5;
    private int startLogicRow = 0;
    private int startClean;
    private boolean modified = false;
    private boolean firstSave = true;
    private int lineBef = 30;
    private int colBef = 80;

    private FileBuffer buffer = new FileBuffer();

    private Position cursor = new Position(startVisualRow, 0);


    private BufferView() throws IOException
    {
        startScreen();
        drawBox();
        drawInfoCursor();
        updateCursor();
        readInput();
    }


    /**
     * Inicializa o term
     */
    private void startScreen()
    {
        terminal = createSwingTerminal(80, 30);
        terminal.moveCursor(0, startVisualRow);
        terminal.enterPrivateMode();
    }


    /**
     * L&ecirc; o input do utilizador a cada 20ms.
     * <p>
     * Verifica sempre se houve mudan&ccedil;a no tamanho no terminal, para poder voltar a reimprir tudo. Caso isso n&atilde;o aconte&ccedil;a s&oacute; chama outras fun&ccedil;&otilde;es quando h&aacute; teclas pressionadas.
     */
    private void readInput() {
        while (true)
        {

            checkDiff();

            Key in = terminal.readInput();


            if (in != null)
            {
            clearMessage();


                switch (in.getKind())
                {
                    case Escape:
                        terminal.exitPrivateMode();
                        terminal.flush();
                        return;
                    case Enter:
                        buffer.insert("\n");
                        modified = true;
                        break;
                    case Tab:
                        buffer.insert("\t");
                        modified = true;
                        break;
                    case Backspace:
                        buffer.delete();
                        modified = true;
                        break;
                    case ArrowRight:
                        buffer.moveRight();
                        break;
                    case ArrowLeft:
                        buffer.moveLeft();
                        break;
                    case ArrowDown:
                        int posD[] = converterB(cursor.getLine()+1, cursor.getCol());
                        if (posD!=null)
                            buffer.move(posD[0], posD[1]);
                        break;
                    case ArrowUp:
                        int posU[] = converterB(cursor.getLine()-1, cursor.getCol());
                        if (posU!=null)
                            buffer.move(posU[0], posU[1]);
                        break;
                    default:
                        if (in.isCtrlPressed()) {
                            if (in.getCharacter() == 's')
                                if(!firstSave)
                                    save();
                                else {
                                    action(2);
                                }
                            else if (in.getCharacter() == 'a') {
                                action(2);
                            }
                            else if (in.getCharacter() == 'o') {
                                action(1);
                            }
                        } else {
                            buffer.insert(in.getCharacter() + "");
                            modified = true;
                        }

                }

                display();
            }

            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    /**
     * Retorna a largura do terminal
     *
     * @return A largura do terminal
     */
    private int getWidth()
    {
        TerminalSize temp = terminal.getTerminalSize();
        return temp.getColumns();
    }





    /**
     * Retorna a altura do terminal
     *
     * @return A altura do terminal
     */
    private int getHeight()
    {
        TerminalSize temp = terminal.getTerminalSize();
        return temp.getRows();
    }





    /**
     *Esta fun&ccedil;&atilde;o &eacute; chamada a cada vinte milisegundos que tem como objectivo verificar se houve uma altera&ccedil;&atilde;o no tamanho de terminal.<br>
     *Caso essa mudan&ccedil;a tenha ocorrido, toda a informa&ccedil;&atilde;o &eacute; reimprimida de forma ajustada ao novo tamanho.
     */
    private void checkDiff()
    {
        if((getWidth() != colBef) || (getHeight() != lineBef))
        {
            terminal.clearScreen();
            drawBox();
            scroll();
            colBef = getWidth();
            lineBef = getHeight();
            startClean = startVisualRow;
            modified = true;
            clearAndDraw();
            drawInfoCursor();
            updateCursor();

        }

    }





    /**
     * Esta fun&ccedil;&atilde;o apenas tem a finalidade de chamar outras. Todas as que chama s&atilde;o para actualizar a informa&ccedil;&atilde;o apresentada no terminal de acordo com o conte&ucute;o do buffer.
     */
    private void display()
    {
        startClean = cursor.getLine();
        updateCursor();
        scroll();
        clearAndDraw();
        drawInfoCursor();
        updateCursor();
    }





    /**
     * Altera a vari&aacute;vel startLogicRow quando necess&aacute;rio.
     * <p>
     * Mais precisamente, indica qual a primeira linha l&oacute;gica aser exibida no terminal de acordo com a posi&ccedil;&atilde;o do cursor. <br>
     * Quando o cursor est&acute; na primeira linha do terminal e a primeira linha l&oacute;gica apresentada n&atilde;o &eacute; a primeira do buffer, a vari&aacute;vel &eacute; decrementada.
     * O contr&aacute;rio acontece quando nos encontramos na &uacute;ltima linha do terminal.
     */
    public void scroll()
    {
        updateCursor();

        int finalVisualRow = getHeight()-startVisualRow ;

        if(cursor.getLine() > finalVisualRow)
        {
            startLogicRow++;
            startClean = startVisualRow;
            modified = true;
        }

        if(cursor.getLine() == startVisualRow)
            if(startLogicRow>0)
            {
                startLogicRow--;
                startClean = startVisualRow;
                modified = true;
            }
    }





    /**
     * Limpa e volta a imprimir apensa uma parte do terminal.
     * <p>
     * Antes de mais, esta fun&ccedil;&atilde;o s&acute; modifica alguma coisa quando a vari&aacute;vel boleana modified &eacute; verdadeira. <br>
     * &Eacute; tido em conta a vari&aacute;vel startClean que indica a partir de que linha v&atilde;o ocorrer as mudan&ccedil;as.
     * Em alguns dos casos &eacute; a partir da linha onde se encontra o cursor, noutros, a partir da primeira linha do terminal. <br>
     * Assim, s&oacute; alteramos as partes necessárias, aumentando a efic&aacute;cia do editor.
     *
     */
    private void clearAndDraw()
    {
        if (modified) {

            terminal.setCursorVisible(false);

            //variaveis
            int width = getWidth();
            int finalVisualRow = getHeight() - startVisualRow + 1;
            int posicaoVisual[];


            //limpar
            for (int i = startClean; i < finalVisualRow; i++) {
                for (int j = 0; j < width; j++) {
                    terminal.moveCursor(j, i);
                    terminal.putCharacter(' ');
                }
            }

            //imprimir
            for (int i = startLogicRow; i < buffer.getNumLines(); i++) {
                String phrase = buffer.getString(i);

                for (int j = 0; j < phrase.length(); j++) {
                    String character = phrase.charAt(j) + "";
                    posicaoVisual = converterA(i, j);
                    if (posicaoVisual != null)
                        if (posicaoVisual[0] >= startClean && posicaoVisual[0] < finalVisualRow)
                            show(character, posicaoVisual[0], posicaoVisual[1],2);
                }
            }

            terminal.setCursorVisible(true);

        }
        modified = false;
    }





    /**
     * Desenha o grafismo extremamente b&aacute;sico do editor, como as bordas, o nome do editor e o nome do ficheiro.
     */
    private void drawBox()
    {
        int width = getWidth();
        int height = getHeight();
        String str = "";

        for (int x = 0; x <= width; x++) {
            str += '*';
        }

        show(str,0,0,2);
        show(str,startVisualRow-1,0,2);
        show(str,height-startVisualRow+1,0,2);

        str = buffer.getFileName();
        show("EDITOR DE TEXTO", 2, 3,2);
        if (3 + "EDITOR DE TEXTO".length() + 3 + str.length() + 3 <= width)
            show(str, 2, width - 3 - str.length(),2);
    }






    /**
     * Imprime na nas linhas finais do terminal, encostado &agrave; esquerda, a posi&ccedil;&atilde;o onde se encontra o cursor em rela&ccedil;&atilde;o ao buffer. Antes de isso, limpa a mesma &agrave;rea do terminal para n&atilde;o acontecerem confus&otilde;es.
     */
    private void drawInfoCursor()
    {
        terminal.setCursorVisible(false);

        int height = getHeight();
        String str = "";

        for(int i=0 ; i<20 ; i++)
            str+=" ";

        show(str, height-2, 3,2);

        updateCursor();
        str = "row:" + (cursor.getLine()-startVisualRow + count(0,startLogicRow)) + "  col:" + cursor.getCol();
        show(str, height-2, 3,2);

        terminal.setCursorVisible(true);
    }






    /**
     *Retorna a quantidade de linhas visuais no intervalo de duas linhas l&oacute;gicas pretendidas, sendo a primeira inclu&iacute;da e a &uacute;ltima n&atilde;o.
     *
     * @param initial Primeira linha l&oacute;gica.
     * @param last Ultima linha l&oacute;gica.
     * @return A quantidade de linhas visuais entre duas linhas l&oacute;gicas.
     */
    private int count(int initial, int last)
    {
        int numVisualLines = 0;
        int width = getWidth();

        for (int i=initial ; i<last ; i++) {
            int sizeLine = buffer.getString(i).length();
            int linesLine = (sizeLine / width) + 1;
            numVisualLines += linesLine;
        }
        return  numVisualLines;
    }






    /**
     * Fun&ccedil;&atilde;o chamada sempre que ocorre uma a&ccedil;&atilde;o. Tem como objectivo recolocar o cursor no sitio correcto.
     */
    private  void updateCursor()
    {
        int CursorPos[] = converterA(buffer.getRow(), buffer.getCol());
        if (CursorPos != null)
        {
            cursor.setLine(CursorPos[0]);
            cursor.setCol(CursorPos[1]);
        }
        terminal.moveCursor(cursor.getCol(),cursor.getLine());
    }





    /**
     * Converte as coordenadas l&oacute;gicas em visuais, atrav&eacute;s de c&aacute;lculos matem&aacute;ticos.
     */
    private int[] converterA(int row, int col)
    {

        int width = getWidth();

        int posicao[] = new int[2];
        int visualLine = startVisualRow-1;

        for (int i=startLogicRow ; i<row ; i++)
        {
            int sizeLine = buffer.getString(i).length();
            int linesLine = (sizeLine / width) + 1;
            visualLine += linesLine;
        }

        int lastLine = (col/getWidth()) +1;
        visualLine += lastLine;
        int newCol = col - ((lastLine-1)*width);

        posicao[0] = visualLine;
        posicao[1] = newCol;
        return posicao;
    }

    /**
     * Converte as coordenadas visuais em l&oacute;gicas, atrav&eacute;s de c&aacute;lculos matem&aacute;ticos.
     */
    private int[] converterB(int row, int col)
    {

        int width = getWidth();
        int posicao[] = new int[2];
        int offset = 0;
        int visualLine = startVisualRow-1;

        for (int i=startLogicRow; i<buffer.getNumLines(); i++)
        {

            int sizeLine = buffer.getString(i).length();
            int linesLine = sizeLine / width + 1;

            if (visualLine + linesLine < row)
            {
                visualLine += linesLine;
            }

            else
            {
                while (visualLine <= row)
                {
                    offset += width;
                    visualLine++;
                    if (visualLine == row)
                    {
                        posicao[0] = i;
                        posicao[1] = col + offset - width;
                        return posicao;
                    }
                }
            }
        }
        return null;
    }


    /**
    * Fun&ccedil;&atilde;o auxiliar que permite imprimir texto no termianal.
     *
    * @param str String a ser exibida no terminal.
    * @param row Linha onde vai ser colocado o primeiro caracter.
    * @param col Coluna onde vai ser colocado o primeiro caracter.
     * @param color Cor das letras a serem imprimidas.
    */
    private void show(String str, int row, int col, int color)
    {
        terminal.setCursorVisible(false);

        if ( color == 0 )
            terminal.applyForegroundColor(Color.RED);
        else if (color == 1)
            terminal.applyForegroundColor(Color.GREEN);
        else if ( color == 2 )
            terminal.applyForegroundColor(Color.WHITE);

        int length = str.length();

        for (int i = 0; i < length; i++)
        {
            terminal.moveCursor(col+i, row);
            terminal.putCharacter(str.charAt(i));
        }

        terminal.setCursorVisible(true);
    }




    /**
     * Depois de o utilizador terminar a sua tarefa de abrir/guardar o ficheiro, a informa&ccedil;&atilde;o do buffer necessita de ser (re)imprimida.
     */
    private void back()
    {
        for (int i = 0; i < getWidth(); i++) {
            terminal.moveCursor(i, 2);
            terminal.putCharacter(' ');
        }

        drawBox();
        modified = true;
        startClean = startVisualRow;
        clearAndDraw();
        updateCursor();
    }





    /**
     * Fun&ccedil;&atilde;o chamada quanho h&aacute; a abertura de um ficheiro.
     * <p>
     * Descobre qual a primeira linha l&oacute;gica que deve ser exibida de modo a que a ultima seja vis&iacute;vel no terminal.
     * @return O &iacute;ndice da primeira linha l&oacute;gica a ser exibida.
     */
    private int findStartLogicRow()
    {
        int finalVisualRow = getHeight()-startVisualRow;
        int space = finalVisualRow-startVisualRow;

        int fin = buffer.getNumLines()-1;
        int init = fin;

        while(init>=0) {
            int res = count(init,fin);
            if (res > space) {
                return init + 1;
            }
            init--;
        }

        return 0;
    }





    /**
     * Imprime uma mensagem depois de uma a&ccedil;&atilde;o de abrir/guardar ficheiros. A mensagem &eacute; no canto inferior direito do terminal com a cor adequada relativa ao sucesso ou insucesso.
     *
     * @param flag Indica se houve sucesso ou fracasso.
     */
    private void printMessage(int flag)
    {
        int width = getWidth();
        int height = getHeight();
        String str = "";

        if(flag==0)
            str = "OCORREU UM ERRO";
        else if (flag==1)
            str = "CONCLUIDO COM SUCESSO";

        show(str, height-2, width-str.length()-3,flag);

        int length = str.length();
        str="";

        for (int x = 0; x < length; x++) {
            str += '*';
        }
        show(str,height-1,width-length-3,flag);
        show(str,height-3,width-length-3,flag);

        updateCursor();
    }





    /**
     * Limpa a zona do terminal reservada para as mensagens relativas aos acontecimentos de abrir/guardar ficheiros.
     */
    private void clearMessage()
    {
        int width = getWidth();
        int height = getHeight();
        String str = "";

        for(int i=0; i<25 ; i++)
            str += " ";

        show(str, height-1, width-str.length(),2);
        show(str, height-2, width-str.length(),2);
        show(str, height-3, width-str.length(),2);

    }




    /**
     *A chamada a esta fun&ccedil;&atilde;o acontece para chamar outra fun&ccedil;&atilde;o, que permite guardar um ficheiro num diret&oacute;rio j&aacute; definido, e imprimir a mensagem consoate o reaultado obtido.
     */
    private void save()
    {
        int res = buffer.save();
        printMessage(res);
    }


    /**
     * Fun&ccedil;&atilde;o que permite ao utilizador escolher um diret&oacute;rio para abrir ou guardar um ficheiro.
     * <p>
     * Para guardar s&oacute; &eacute; chamada quando &eacute; a primeira vez que guardamos, ou quando carregamos no atalho respetivo. Para abrir um ficheiro passamos sempre por aqui.
     *
     * @param flag Indica se pretendemos abrir ou guardar um ficheiro.
     */
    private void action(int flag) {

        terminal.clearScreen();

        String saveName = buffer.getFileName();

        int width = getWidth();
        String str = "";

        for (int x = 0; x <= width; x++) {
            str += '*';
        }

        show(str,0,0,2);
        show(str,startVisualRow-1,0,2);

        int row = 2;

        if (flag ==1)
            str = "  Abrir do ficheiro: ";
        if (flag == 2)
            str =  "  Guardar no ficheiro: ";

        show(str,row,0,2);

        int firstCol= str.length();
        int space = width-firstCol -3;
        int lengthDir = 1;

        String path="/";
        show(path, row, firstCol, 2);

        boolean reading=true;
        boolean wantGoBack = false;

        while(reading)
        {
            Key in = terminal.readInput();

            if (in != null)
            {

                switch (in.getKind()) {
                    case Enter:
                        reading = false;
                        terminal.clearScreen();
                        break;
                    case Escape:
                        reading = false;
                        wantGoBack = true;
                        break;
                    case Backspace:
                        if (lengthDir > 1) {
                            lengthDir--;
                            if (space > lengthDir) {
                                path = path.substring(0, path.length() - 1);
                                show(path, row, firstCol, 2);
                                int posClean = firstCol + lengthDir;
                                show(" ",row,posClean,2);
                                terminal.moveCursor(posClean,row);
                            } else {
                                path = path.substring(0, path.length() - 1);
                                show(path.substring(path.length() - space , path.length()), row, firstCol, 2);
                            }
                        }
                        break;
                    case NormalKey:
                        lengthDir++;
                        if (space  > lengthDir) {
                            String c = in.getCharacter() + "";
                            path += c;
                            show(path, row, firstCol, 2);
                        } else {
                            String c = in.getCharacter() + "";
                            path += c;
                            show(path.substring(path.length() - space , path.length()), row, firstCol, 2);
                        }
                        break;

                }
            }
        }

        if(!wantGoBack)
        {
            String Allpath= "/home/meira" + path;
            buffer.setPath(Allpath);

            int res = -1;

            if(flag == 1)
                res = buffer.open();
            if(flag == 2)
                res = buffer.save();

            if(res == 1)
            {
                firstSave = false;
            }
            if(res == 0)
            {
                buffer.setPath(saveName);
            }
            printMessage(res);

            if( flag == 1)
                startLogicRow = findStartLogicRow();

        }
        back();
    }





    public static void main(String args[]) throws IOException
    {
        BufferView open = new BufferView();
    }


}