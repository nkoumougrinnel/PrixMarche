package cm.supptic.prixmarche;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrixChartView extends View {

    public static class PointPrix {
        public final String label;
        public final double valeur;

        public PointPrix(String label, double valeur) {
            this.label = label;
            this.valeur = valeur;
        }
    }

    private final List<PointPrix> points = new ArrayList<>();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PrixChartView(Context context) {
        super(context);
        init();
    }

    public PrixChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrixChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint.setColor(Color.parseColor("#1E3A8A"));
        barPaint.setStyle(Paint.Style.FILL);

        axisPaint.setColor(Color.parseColor("#CBD5E1"));
        axisPaint.setStrokeWidth(3f);

        textPaint.setColor(Color.parseColor("#334155"));
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(Color.parseColor("#0D9488"));
        valuePaint.setTextSize(28f);
        valuePaint.setFakeBoldText(true);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        emptyPaint.setColor(Color.parseColor("#64748B"));
        emptyPaint.setTextSize(34f);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setDonnees(List<PointPrix> nouvellesDonnees) {
        points.clear();
        if (nouvellesDonnees != null) {
            int limite = Math.min(nouvellesDonnees.size(), 6);
            for (int i = 0; i < limite; i++) {
                points.add(nouvellesDonnees.get(i));
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        if (points.isEmpty()) {
            canvas.drawText("Aucune donnée graphique disponible", width / 2f, height / 2f, emptyPaint);
            return;
        }

        float paddingHorizontal = 36f;
        float topArea = 56f;
        float bottomArea = 64f;
        float chartTop = topArea;
        float chartBottom = height - bottomArea;
        float chartHeight = Math.max(chartBottom - chartTop, 40f);

        // Ligne de base
        canvas.drawLine(paddingHorizontal, chartBottom, width - paddingHorizontal, chartBottom, axisPaint);

        double maxVal = 1.0;
        for (PointPrix p : points) {
            if (p.valeur > maxVal) {
                maxVal = p.valeur;
            }
        }

        int count = points.size();
        float slotWidth = (width - 2 * paddingHorizontal) / count;
        float barWidth = Math.min(slotWidth * 0.55f, 110f);

        for (int i = 0; i < count; i++) {
            PointPrix p = points.get(i);
            float centerX = paddingHorizontal + slotWidth * i + slotWidth / 2f;
            float ratio = (float) (p.valeur / maxVal);
            float barHeight = Math.max(ratio * (chartHeight - 20f), 12f);
            float left = centerX - barWidth / 2f;
            float top = chartBottom - barHeight;
            float right = centerX + barWidth / 2f;

            RectF rect = new RectF(left, top, right, chartBottom);
            canvas.drawRoundRect(rect, 14f, 14f, barPaint);

            String valText = (p.valeur % 1 == 0)
                    ? String.valueOf((long) p.valeur)
                    : String.valueOf(p.valeur);
            canvas.drawText(valText, centerX, Math.max(top - 10f, 30f), valuePaint);

            String labelCourt = p.label.length() > 9 ? p.label.substring(0, 9) + "…" : p.label;
            canvas.drawText(labelCourt, centerX, height - 18f, textPaint);
        }
    }
}