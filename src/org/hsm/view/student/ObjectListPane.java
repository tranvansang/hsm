package org.hsm.view.student;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hsm.control.Control;
import org.hsm.model.hedspiObject.HedspiObject;
import org.hsm.model.hedspiObject.HedspiTable;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public abstract class ObjectListPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultListModel<HedspiObject> model;
	private JList<HedspiObject> list;
	private JButton btnRefresh;
	private JLabel lblClassList;
	private JTextField txtEnterSortPattern;
	private JCheckBox chckbxInstant;
	private final DocumentListener searchBoxListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			resort();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			resort();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			resort();
		}
	};

	/**
	 * Create the panel.
	 */
	public ObjectListPane() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(12dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		txtEnterSortPattern = new JTextField();
		model = new DefaultListModel<>();

		lblClassList = DefaultComponentFactory.getInstance().createLabel(
				"Objects list");
		add(lblClassList, "1, 2, 7, 1");

		// txtEnterSortPattern.getDocument()
		// .addDocumentListener(searchBoxListener);

		JButton btnSortBox = new JButton("Sort");
		add(btnSortBox, "1, 4, 3, 1, left, default");
		btnSortBox.setToolTipText("Smart ordering");
		btnSortBox.setMnemonic('s');
		btnSortBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resort();
			}
		});

		chckbxInstant = new JCheckBox("Instant");
		add(chckbxInstant, "4, 4, 4, 1");
		chckbxInstant.setToolTipText("Uncheck for weak computer");
		chckbxInstant.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxInstant.isSelected()) {
					// if
					// (txtEnterSortPattern.getDocument().hasEventListener(searchBoxListener))
					txtEnterSortPattern.getDocument().addDocumentListener(
							searchBoxListener);
				} else {
					txtEnterSortPattern.getDocument().removeDocumentListener(
							searchBoxListener);
				}
			}
		});
		chckbxInstant.setMnemonic('i');
		chckbxInstant.setSelected(true);

		add(txtEnterSortPattern, "1, 6, 7, 1");
		txtEnterSortPattern.setToolTipText("Ordering pattern");
		txtEnterSortPattern.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtEnterSortPattern.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtEnterSortPattern.select(0, 0);
			}
		});
		txtEnterSortPattern.setText("Enter sort pattern here");
		txtEnterSortPattern.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "1, 8, 7, 1, fill, fill");

		list = new JList<HedspiObject>(model);
		lblClassList.setLabelFor(list);
		list.setToolTipText("Objects list");
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				HedspiObject value = list.getSelectedValue();
				if (value != null)
					selectValue(value);
			}
		});
		scrollPane.setViewportView(list);

		JButton btnNew = new JButton("+");
		add(btnNew, "1, 10, left, default");
		btnNew.setToolTipText("Get new object from server");
		btnNew.setMnemonic('n');
		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HedspiObject value = newElement();
				if (value != null) {
					model.addElement(value);
					list.setSelectedValue(value, true);
				} else {
					JOptionPane.showMessageDialog(Control.getInstance()
							.getMainWindow(),
							"Failed to create new element\nMessage: "
									+ Control.getInstance().getQueryMessage(),
							"Failed to create new element",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		JButton btnRemove = new JButton("");
		add(btnRemove, "3, 10, left, default");
		btnRemove
				.setIcon(new ImageIcon(
						ObjectListPane.class
								.getResource("/javax/swing/plaf/metal/icons/ocean/paletteClose-pressed.gif")));
		btnRemove.setToolTipText("Remove selected");
		btnRemove.setMnemonic('e');
		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HedspiObject value = list.getSelectedValue();
				if (value == null)
					return;
				String message = removeElement(value);
				if (message == null) {
					model.removeElement(value);
					list.clearSelection();
				} else
					JOptionPane.showMessageDialog(Control.getInstance()
							.getMainWindow(), "Delete failed.\nMessage: "
							+ message, "Delete failed",
							JOptionPane.ERROR_MESSAGE);
			}
		});

		btnRefresh = new JButton("R");
		add(btnRefresh, "5, 10, left, default");
		btnRefresh.setToolTipText("Refresh list");
		btnRefresh.setMnemonic('r');
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		JButton btnExport = new JButton("");
		add(btnExport, "7, 10, left, default");
		btnExport
				.setIcon(new ImageIcon(
						ObjectListPane.class
								.getResource("/javax/swing/plaf/metal/icons/ocean/floppy.gif")));
		btnExport.setToolTipText("Export to html format");
		btnExport.setMnemonic('x');
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = getTitle();
				if (title != null) {
					HedspiTable hedspiTable = new HedspiTable(title, "Name");
					for (int i = 0; i < model.getSize(); i++)
						hedspiTable.addValue(model.getElementAt(i).getName());
					hedspiTable.writeToHtmlWithMessageDialog();
				}
			}
		});

		refresh();
	}

	public abstract String getTitle();

	public ObjectListPane(String label) {
		this();
		lblClassList.setText(label);
	}

	/**
	 * Hàm này sẽ được gọi khi đối tượng được chọn thay đổi và khác
	 * <code>null</code>. Tức khi gọi hàm đã luôn đảm bảo
	 * <code>value != null</code>.
	 * 
	 * @param value
	 *            đối tượng được chọn, đảm bảo khác <code>null</code>.
	 */
	public abstract void selectValue(HedspiObject value);

	/**
	 * Trả về đối tượng mới thêm vào, nếu lấy về không thành công thì trả về
	 * <code>null</code>. Phần tử <code>null</code> sẽ không được thêm vào danh
	 * sách. Phần tử khác <code>null</code> sẽ được thêm vào.
	 * 
	 * @return giá trị phần tử mới. <code>null</code> nếu không thành công.
	 */
	public abstract HedspiObject newElement();

	/**
	 * Hàm này được gọi khi người dùng xóa đối tượng.
	 * 
	 * @param value
	 *            khi gọi hàm này đã đảm bảo cho <code>value != null</code>
	 * @return trả về <code>null</code> nếu thành công. Ngược lại, hãy trả về
	 *         thông báo lỗi.
	 */
	public abstract String removeElement(HedspiObject value);

	/**
	 * Reload CSDL. Ngoài ra, hàm này cũng được gọi khi
	 * {@link #ObjectListPane() khởi tạo} instance.
	 * 
	 * @return danh sách các objects, nếu lỗi thì trả về <code>null</code> sẽ có
	 *         thông báo hiện lên và danh sách không được cập nhật vào hiển thị.
	 *         Lưu ý rằng <code>null</code> khác với danh sách rỗng.
	 */
	public abstract HedspiObject[] getRefresh();

	public void refresh() {
		HedspiObject[] arr = getRefresh();
		if (arr == null) {
			JOptionPane.showMessageDialog(
					Control.getInstance().getMainWindow(),
					"Cannot get list of objects\nMessage: "
							+ Control.getInstance().getQueryMessage(),
					"Refresh failed", JOptionPane.WARNING_MESSAGE);
			return;
		}
		model.clear();
		for (HedspiObject it : arr)
			model.addElement(it);
	}

	private void resort() {
		ArrayList<HedspiObject> list = new ArrayList<>();
		for (int i = 0; i < model.getSize(); i++)
			list.add(model.getElementAt(i));
		model.removeAllElements();
		Collections.sort(list, new Comparator<HedspiObject>() {

			@Override
			public int compare(HedspiObject arg0, HedspiObject arg1) {
				String text = txtEnterSortPattern.getText().toLowerCase();
				// String a = arg0.toString().toLowerCase();
				// String b = arg1.toString().toLowerCase();
				// int t = StringUtils.getLevenshteinDistance(a, text) -
				// StringUtils.getLevenshteinDistance(b, text);
				// if (t == 0)
				// return a.compareTo(b);
				// return t;
				return smartCompare(arg0.toString(), arg1.toString(), text);
			}
		});

		for (HedspiObject it : list)
			model.addElement(it);
	}

	private int smartCompare(String arg0, String arg1, String text) {

		int t1 = getDistance(arg0, text);
		int t2 = getDistance(arg1, text);
		if (t1 != t2)
			return t2 - t1;
		return arg0.toString().compareToIgnoreCase(arg1.toString());
	}

	private int getDistance(String string, String text) {
		int[][] f = new int[string.length() + 1][text.length() + 1];
		for (int i = 0; i <= string.length(); i++)
			for (int j = 0; j <= text.length(); j++)
				f[i][j] = 0;
		for (int i = 1; i <= string.length(); i++)
			for (int j = 1; j <= text.length(); j++) {
				f[i][j] = Math.max(f[i - 1][j], f[i][j - 1]);
				char c1 = string.charAt(i - 1);
				char c2 = text.charAt(j - 1);
				c1 = Character.toLowerCase(c1);
				c2 = Character.toLowerCase(c2);
				if (c1 == c2)
					f[i][j] = Math.max(f[i][j], f[i - 1][j - 1] + 1);
			}
		return f[string.length()][text.length()];
	}
}