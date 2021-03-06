package org.hsm.view.student;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hsm.control.Control;
import org.hsm.model.hedspiObject.HedspiObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * @author haidang-ubuntu
 * 
 */
public abstract class HedspiComboBox extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<HedspiObject> comboBoxModel;
	private JComboBox<HedspiObject> comboBox;
	private JButton btnR;

	/**
	 * Create the panel.
	 */
	public HedspiComboBox() {
		setLayout(new FormLayout(
				new ColumnSpec[] { ColumnSpec.decode("default:grow"),
						ColumnSpec.decode("19dlu"), },
				new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

		comboBoxModel = new DefaultComboBoxModel<>();
		comboBox = new JComboBox<>(comboBoxModel);
		comboBox.setMinimumSize(new Dimension(10, 10));
		comboBox.setToolTipText("Choose object");
		add(comboBox, "1, 1, fill, default");

		btnR = new JButton("R");
		btnR.setToolTipText("Refresh objects list");
		btnR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		add(btnR, "2, 1");

		refresh();
	}

	public abstract HedspiObject[] getValues();

	public void setSelectedValue(int id) {
		for (int i = 0; i < comboBoxModel.getSize(); i++)
			if (comboBoxModel.getElementAt(i).getId() == id) {
				comboBox.setSelectedIndex(i);
				break;
			}
	}

	/**
	 * Get id of selected object
	 * 
	 * @return id selected object or -1 when none is selected
	 */
	public int getSelectedValue() {
		HedspiObject obj = (HedspiObject) comboBoxModel.getSelectedItem();
		if (obj == null)
			return -1;
		else
			return obj.getId();
	}

	public HedspiObject getSelectedObject() {
		return (HedspiObject) comboBoxModel.getSelectedItem();
	}

	public JComboBox<HedspiObject> getComboBox() {
		return comboBox;
	}

	public void refresh() {
		// save current id
		HedspiObject obj = (HedspiObject) comboBox.getSelectedItem();

		comboBoxModel.removeAllElements();
		HedspiObject[] values = getValues();
		if (values == null)
			JOptionPane.showMessageDialog(
					Control.getInstance().getMainWindow(),
					"Refresh failed\nMessage: "
							+ Control.getInstance().getQueryMessage(),
					"Refresh failed", JOptionPane.WARNING_MESSAGE);
		else
			for (HedspiObject it : values)
				comboBoxModel.addElement(it);

		if (obj != null)
			setSelectedValue(obj.getId());
	}

	public void removeObject(HedspiObject selected) {
		comboBoxModel.removeElement(selected);
	}

	public void addObject(HedspiObject it) {
		comboBoxModel.addElement(it);
	}
}
