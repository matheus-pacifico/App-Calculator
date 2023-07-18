package com.pacifico.mvm.calculadora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button numeroZero, numeroUm, numeroDois, numeroTres, numeroQuatro, numeroCinco, numeroSeis, numeroSete,
            numeroOito, numeroNove, virgula, soma, subtracao, multiplicacao, divisao, igual, botao_limpar, parenteseA,
            parenteseB, numeroPi, raiz, expoente, fatorial;

    private TextView txtExpressao, txtResultado;
    private ImageView backspace;
    private static final ForegroundColorSpan FOREGROUND_COLOR_SPAN_AZUL = new ForegroundColorSpan(Color.parseColor("#679BC8"));
    private final Context applicationContext = getApplicationContext();
    private String numeroAtual = "";
    private static boolean currentOperationIsInfinity = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarComponentes();
        Objects.requireNonNull(getSupportActionBar()).hide();

        numeroZero.setOnClickListener(view -> addCharacterToExpression("0", false));
        numeroUm.setOnClickListener(view -> addCharacterToExpression("1", false));
        numeroDois.setOnClickListener(view -> addCharacterToExpression("2", false));
        numeroTres.setOnClickListener(view -> addCharacterToExpression("3", false));
        numeroQuatro.setOnClickListener(view -> addCharacterToExpression("4", false));
        numeroCinco.setOnClickListener(view -> addCharacterToExpression("5", false));
        numeroSeis.setOnClickListener(view -> addCharacterToExpression("6", false));
        numeroSete.setOnClickListener(view -> addCharacterToExpression("7", false));
        numeroOito.setOnClickListener(view -> addCharacterToExpression("8", false));
        numeroNove.setOnClickListener(view -> addCharacterToExpression("9", false));
        numeroPi.setOnClickListener(view -> addCharacterToExpression("π", false));
        virgula.setOnClickListener(view -> addCharacterToExpression(",", false));
        fatorial.setOnClickListener(view -> addCharacterToExpression("!", false));

        soma.setOnClickListener(view -> addCharacterToExpression("+", true));
        subtracao.setOnClickListener(view -> addCharacterToExpression("–", true));
        multiplicacao.setOnClickListener(view -> addCharacterToExpression("×", true));
        divisao.setOnClickListener(view -> addCharacterToExpression("÷", true));
        parenteseA.setOnClickListener(view -> addCharacterToExpression("(", true));
        parenteseB.setOnClickListener(view -> addCharacterToExpression(")", true));
        raiz.setOnClickListener(view -> addCharacterToExpression("√(", true));
        expoente.setOnClickListener(view -> addCharacterToExpression("^", true));

        botao_limpar.setOnClickListener(view -> {
            txtExpressao.setText("");
            limparResultado();
            limparNumeroAtual();
        });

        backspace.setOnClickListener(view -> {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getTxtExpressionText());

            if (spannableStringBuilder.length() > 0) {
                int sizeTxtExpressao = spannableStringBuilder.length();
                spannableStringBuilder.delete(sizeTxtExpressao - 1, sizeTxtExpressao);
                txtExpressao.setText(spannableStringBuilder);
                corrigirNumeroAtual();
            }
        });

        igual.setOnClickListener(view -> {
            if (!expressionIsValid(getTxtExpressaoToString())) {
                showToastWithMessage(applicationContext.getString(R.string.invalid_format_used));
            } else if (!getTxtResultadoToString().isEmpty()) {
                txtExpressao.setText(getTxtResultadoToString());
                limparResultado();
                limparNumeroAtual();
            } else if (currentOperationIsInfinity) {
            	showToastWithMessage(applicationContext.getString(R.string.infinity_result));
            }
        });

        txtExpressao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cS, int s, int b, int c)  { /*nothing*/ }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 19) {
                    txtExpressao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                } else if (charSequence.length() > 14) {
                    txtExpressao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                } else {
                    txtExpressao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
                }

                if (expressionIsValid(String.valueOf(charSequence))) {
                    calcularResultado();
                } else {
                    limparResultado();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { /*nothing*/ }
        });
    }

    private void iniciarComponentes() {
        numeroZero = findViewById(R.id.numero_zero);
        numeroUm = findViewById(R.id.numero_um);
        numeroDois = findViewById(R.id.numero_dois);
        numeroTres = findViewById(R.id.numero_tres);
        numeroQuatro = findViewById(R.id.numero_quatro);
        numeroCinco = findViewById(R.id.numero_cinco);
        numeroSeis = findViewById(R.id.numero_seis);
        numeroSete = findViewById(R.id.numero_sete);
        numeroOito = findViewById(R.id.numero_oito);
        numeroNove = findViewById(R.id.numero_nove);
        virgula = findViewById(R.id.virgula);
        soma = findViewById(R.id.soma);
        subtracao = findViewById(R.id.subtracao);
        multiplicacao = findViewById(R.id.multiplicacao);
        divisao = findViewById(R.id.divisao);
        igual = findViewById(R.id.igual);
        botao_limpar = findViewById(R.id.bt_limpar);
        txtExpressao = findViewById(R.id.txt_expressao);
        txtResultado = findViewById(R.id.txt_resultado);
        backspace = findViewById(R.id.backspace);
        parenteseA = findViewById(R.id.parentesea);
        parenteseB = findViewById(R.id.parenteseb);
        numeroPi = findViewById(R.id.numero_pi);
        raiz = findViewById(R.id.raiz);
        expoente = findViewById(R.id.expoente);
        fatorial = findViewById(R.id.fatorial);
    }

    private String getTxtExpressaoToString() {
        return txtExpressao.getText().toString().trim();
    }

    private CharSequence getTxtExpressionText() {
        return txtExpressao.getText();
    }

    private String getTxtResultadoToString() {
        return txtResultado.getText().toString().trim();
    }

    private void addCharacterToExpression(String string, boolean characterIsOperator) {
        boolean characaterIsFatorial = string.equals("!");
        if (!characterIsOperator || characaterIsFatorial) {
            String numeroAtualSemVirgula = numeroAtual.replaceFirst(",", "");
            if (characaterIsFatorial && numeroAtualSemVirgula.length() < 16) {
                addCharacterWithDefaultColor(string);
            } else if (numeroAtualSemVirgula.length() < 15) {
                if (!(string.equals(",") && numeroAtual.contains(","))) {
                	addCharacterWithDefaultColor(string);
                }
            } else {
                showToastWithMessage(applicationContext.getString(R.string.max_num_characters));
            }
        } else {
            addCharacterWithBlueColor(string);
            limparNumeroAtual();
        }
    }

    private boolean expressionIsValid(String expressao) {
        if (expressao.isEmpty()) {
            return false;
        }
        if (expressao.matches("[0-9(),]+")) {
            return false;
        }
        char ultimoCaracter = expressao.charAt(expressao.length() - 1);
        return String.valueOf(ultimoCaracter).matches("[0-9π!)]");
    }

    private void showToastWithMessage(String message) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
    }

    private void corrigirNumeroAtual() {
        int comprimentoNumeroAtual = numeroAtual.length();
        if (comprimentoNumeroAtual > 0) {
            numeroAtual = numeroAtual.substring(0, comprimentoNumeroAtual - 1);
        } else {
            String expressao = getTxtExpressaoToString();
            numeroAtual = numeroAtualCorrigido(expressao);
        }
    }

    private String numeroAtualCorrigido(String expressao) {
        StringBuilder numero_atual = new StringBuilder();
        for (int i = 0; i < expressao.length(); i++) {
            char caractere = expressao.charAt(i);
            if (Character.isDigit(caractere) || caractere == '!' || caractere == ',' || caractere == 'π') {
                numero_atual.append(caractere);
            } else {
                numero_atual = new StringBuilder();
            }
        }
        return numero_atual.toString();
    }

    private String substituirFatorial(String expressao) {
        StringBuilder numero_atual = new StringBuilder();
        boolean isFloatNumber = false;

        for (int i = 0; i < expressao.length(); i++) {
            char caractere = expressao.charAt(i);

            if (Character.isDigit(caractere)) {
                numero_atual.append(caractere);
            } else if (caractere == '.') {
            	isFloatNumber = true;
        	} else if (caractere == '!') {
        		if (!isFloatNumber && numero_atual.length() > 0) {
                    int numero = Integer.parseInt(numero_atual.toString());
                    long fatorial = calcularFatorial(numero);
                    String resultado = Long.toString(fatorial);
                    expressao = expressao.replaceFirst(numero_atual.toString() + '!', resultado);
                    i = i - numero_atual.length() + resultado.length();
                    if (i < expressao.length() && expressao.charAt(i) == '!') {
                    	i = i - resultado.length();
                    }
                    
                    i--;
            	}
                if (i >= expressao.lastIndexOf('!')) {
                    break;
                }
            	numero_atual = new StringBuilder();
            } else {
                numero_atual = new StringBuilder();
                isFloatNumber = false;
            }
        }
        return expressao;
    }
    
    private void addCharacterWithDefaultColor(String character) {
    	txtExpressao.append(character);
        numeroAtual += character;
    }

    private void addCharacterWithBlueColor(String character) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getTxtExpressionText());
        spannableStringBuilder.append(character);
        int posicaoCharAzul = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(FOREGROUND_COLOR_SPAN_AZUL, posicaoCharAzul - 1, posicaoCharAzul, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtExpressao.setText(spannableStringBuilder);
    }

    private long calcularFatorial(int numero) {
        if (numero < 0) {
            throw new IllegalArgumentException("The number can not be negative.");
        }
        long fatorial = 1;
        for (int i = 2; i <= numero; i++) {
            fatorial *= i;
        }
        return fatorial;
    }
    
    private void limparNumeroAtual() {
        numeroAtual = "";
    }

    private void limparResultado() {
        txtResultado.setText("");
    }

    private void calcularResultado() {
        String expressao = getTxtExpressaoToString().replaceAll(",", ".").replaceAll("÷", "/").replaceAll("×", "*").replaceAll("–",  "-").replaceAll("√", "sqrt");
        
        try {
        	if (expressao.contains("!")) {
        		expressao = substituirFatorial(expressao);
	        }
            Expression expressionBuilder = new ExpressionBuilder(expressao).build();
            float floatResultado = (float) expressionBuilder.evaluate();
            currentOperationIsInfinity = Float.isInfinite(floatResultado);
            if (currentOperationIsInfinity) {
            	limparResultado();
            	return;
            }
            long longResultado = (long) floatResultado;

            if (floatResultado == (float)longResultado) {
                txtResultado.setText(String.valueOf(longResultado).replaceAll("-", "–"));
            } else {
                txtResultado.setText(String.valueOf(floatResultado).replaceAll("\\.", ",").replaceAll("-", "–"));
            }
        } catch (Exception e) {
            limparResultado();
        }
    }

}
