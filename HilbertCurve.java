import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class HilbertCurve {
	private Polygon h;		// stores curve points
	private int n;			// order of curve
	private JFrame w, o;	// main window, slider window
	private Insets insets;	// used for sizing
	private JPanel d;		// main drawing panel


	public HilbertCurve() {
		w = new JFrame("Hilbert Curve - http://github.com/nhasdarjian");
		insets = w.getInsets();
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setSize(insets.left + insets.right + 725, insets.bottom + insets.top + 725);
		w.setResizable(false);	// easier to work with

		int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int sHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		w.setLocation((sWidth / 2) - (w.getWidth() / 2), (sHeight / 2) - (w.getHeight() / 2));
		// OS-independent relative positioning (setRelativeLocationTo(null) gave me issues on Linux)


		d = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;	// for changing stroke of lines
				h = new Polygon();				// create new storage space for points
				g2.setStroke(new BasicStroke(w.getWidth() / (float) (Math.pow(n, 2) * 20))); // relative stroke
				hilbert(0, 0, d.getWidth(), 0, 0, d.getHeight(), n); // compute points, store in h
				g2.drawPolyline(h.xpoints, h.ypoints, h.npoints);
			}
		};
		d.setForeground(Color.WHITE);	// draw white
		d.setBackground(Color.BLACK);	// on black background

		o = new JFrame("Options");
		o.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		o.setSize(100, 400);
		o.setResizable(false);
		o.setLocation(w.getX() + w.getWidth() + 10, w.getY() + (w.getHeight() / 4)); // set relative location

		JSlider j = new JSlider(JSlider.VERTICAL, 1, 8, 3); // values > 8 are not distinguishable
		j.addChangeListener((ChangeEvent e) -> {
			JSlider src = (JSlider) e.getSource();
			if (!src.getValueIsAdjusting()) {
				n = src.getValue();
				d.repaint();
			}		// set order of curve to value of slider
		});
		j.setMajorTickSpacing(1);	// ensures integer value for order
		j.setSnapToTicks(true);
		j.setPaintLabels(true);

		n = j.getValue();

		w.setContentPane(d);
		o.setContentPane(j);
		w.setVisible(true);
		o.setVisible(true);		// initialize everything
	}

	public void hilbert(float x, float y, float xi, float xj, float yi, float yj, int n) {
		if (n <= 0) h.addPoint((int) (x + (xi + yi) / 2), (int) (y + (xj + yj) / 2));
		else {
			hilbert(x, y, yi / 2, yj / 2, xi / 2, xj / 2, n - 1);
			hilbert(x + xi / 2, y + xj / 2, xi / 2, xj / 2, yi / 2, yj / 2, n - 1);
			hilbert(x + xi / 2 + yi / 2, y + xj / 2 + yj / 2, xi / 2, xj / 2, yi / 2, yj / 2, n - 1);
			hilbert(x + xi / 2 + yi, y + xj / 2 + yj, -yi / 2, -yj / 2, -xi / 2, -xj / 2, n - 1);
		}
	} // Hilbert algorithm by Andrew Cumming at Napier University Edinburgh

	public static void main(String[] args) throws Exception {
		EventQueue.invokeAndWait(() -> new HilbertCurve());
	}
}