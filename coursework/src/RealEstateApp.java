// Імпорти для GUI, таблиць, подій, файлів і колекцій
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// Перерахування: типи послуг та статуси наявності
enum ServiceType { ПРОДАЖ, ОРЕНДА, ПІДБІР }
enum AvailabilityStatus { НА_ПРОДАЖ, ПРОДАНА, В_ОРЕНДІ }

// Інтерфейси для розширення (вивід та фільтрація)
interface Printable { void print(); }
interface Filterable { boolean matches(FilterCriteria criteria); }

// Критерії фільтрації
class FilterCriteria {
    String type, location;
    double minPrice, maxPrice;
    AvailabilityStatus status;

    public FilterCriteria(String type, String location, double minPrice, double maxPrice, AvailabilityStatus status) {
        this.type = type;
        this.location = location;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.status = status;
    }
}

// Клас Рієлтор: дані про співробітника агентства
class Realtor implements Printable {
    private String firmName;
    private String fullName;
    private String address;
    private String phone;
    private ServiceType serviceType;

    public Realtor(String firmName, String fullName, String address, String phone, ServiceType serviceType) {
        this.firmName = firmName;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.serviceType = serviceType;
    }
    // Гетери/сетери
    public String getFirmName() { return firmName; }
    public String getFullName() { return fullName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public ServiceType getServiceType() { return serviceType; }

    public void setFirmName(String v) { firmName = v; }
    public void setFullName(String v) { fullName = v; }
    public void setAddress(String v) { address = v; }
    public void setPhone(String v) { phone = v; }
    public void setServiceType(ServiceType v) { serviceType = v; }

    @Override
    public void print() {
        System.out.println(firmName + " | " + fullName + " | " + address + " | " + phone + " | " + serviceType);
    }
}

// Клас Клієнт: дані про клієнта агентства
class Client implements Printable {
    private String fullName;
    private String address;
    private String phone;
    private ServiceType serviceType;

    public Client(String fullName, String address, String phone, ServiceType serviceType) {
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.serviceType = serviceType;
    }

    // Гетери/сетери
    public String getFullName() { return fullName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public ServiceType getServiceType() { return serviceType; }

    public void setFullName(String v) { fullName = v; }
    public void setAddress(String v) { address = v; }
    public void setPhone(String v) { phone = v; }
    public void setServiceType(ServiceType v) { serviceType = v; }

    @Override
    public void print() {
        System.out.println(fullName + " | " + address + " | " + phone + " | " + serviceType);
    }
}

// Клас Нерухомість: дані про об’єкт + поле попиту
class Property implements Printable, Filterable {
    private double area;
    private int floor;
    private int rooms;
    private int yearBuilt;
    private double price;
    private AvailabilityStatus availability;
    private String address;
    private String location; // для запиту "популярна локація"
    private String type;     // тип нерухомості (квартира/будинок тощо)
    private int interestCount = 0; // "попит": збільшується при зацікавленні клієнта

    public Property(String type, String location, String address, double price, AvailabilityStatus availability,
                    double area, int floor, int yearBuilt, int rooms) {
        this.type = type;
        this.location = location;
        this.address = address;
        this.price = price;
        this.availability = availability;
        this.area = area;
        this.floor = floor;
        this.yearBuilt = yearBuilt;
        this.rooms = rooms;
    }

    // Гетери/сетери
    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public double getPrice() { return price; }
    public AvailabilityStatus getAvailability() { return availability; }
    public double getArea() { return area; }
    public int getFloor() { return floor; }
    public int getYearBuilt() { return yearBuilt; }
    public int getRooms() { return rooms; }
    public int getInterestCount() { return interestCount; }

    public void setType(String v) { type = v; }
    public void setLocation(String v) { location = v; }
    public void setAddress(String v) { address = v; }
    public void setPrice(double v) { price = v; }
    public void setAvailability(AvailabilityStatus v) { availability = v; }
    public void setArea(double v) { area = v; }
    public void setFloor(int v) { floor = v; }
    public void setYearBuilt(int v) { yearBuilt = v; }
    public void setRooms(int v) { rooms = v; }

    // Метод для позначення попиту
    public void markInterest() { interestCount++; }

    @Override
    public boolean matches(FilterCriteria c) {
        if (c == null) return true;
        boolean okType = c.type == null || c.type.isEmpty() || type.equalsIgnoreCase(c.type);
        boolean okLoc  = c.location == null || c.location.isEmpty() || location.equalsIgnoreCase(c.location);
        boolean okPrice = (c.minPrice <= 0 || price >= c.minPrice) && (c.maxPrice <= 0 || price <= c.maxPrice);
        boolean okStatus = c.status == null || availability == c.status;
        return okType && okLoc && okPrice && okStatus;
    }

    @Override
    public void print() {
        System.out.println(type + ", " + location + ", " + address + ", " + price + ", " + availability);
    }
}

// Менеджер даних: списки і запити/статистика
class Agency {
    private String name;

    List<Property> properties = new ArrayList<>();
    List<Client> clients = new ArrayList<>();
    List<Realtor> realtors = new ArrayList<>();

    public Agency(String name) { this.name = name; }
    public String getName() { return name; }

    // CRUD для нерухомості
    public void addProperty(Property p) { properties.add(p); }
    public void removeProperty(int index) { if (index >= 0 && index < properties.size()) properties.remove(index); }

    // CRUD для клієнтів
    public void addClient(Client c) { clients.add(c); }
    public void removeClient(int index) { if (index >= 0 && index < clients.size()) clients.remove(index); }

    // CRUD для рієлторів
    public void addRealtor(Realtor r) { realtors.add(r); }
    public void removeRealtor(int index) { if (index >= 0 && index < realtors.size()) realtors.remove(index); }

    // Збереження нерухомості у CSV
    public void savePropertiesToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Property p : properties) {
                writer.println(p.getType() + "," + p.getLocation() + "," + p.getAddress() + "," + p.getPrice() + "," +
                        p.getAvailability() + "," + p.getArea() + "," + p.getFloor() + "," + p.getYearBuilt() + "," +
                        p.getRooms() + "," + p.getInterestCount());
            }
            JOptionPane.showMessageDialog(null, "Дані нерухомості збережено у файл: " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Помилка запису у файл.");
        }
    }

    // Запити/статистика
    public double averagePrice() {
        if (properties.isEmpty()) return 0;
        double sum = 0;
        for (Property p : properties) sum += p.getPrice();
        return sum / properties.size();
    }

    public String mostPopularArea() {
        Map<String, Integer> count = new HashMap<>();
        for (Property p : properties) {
            count.put(p.getLocation(), count.getOrDefault(p.getLocation(), 0) + 1);
        }
        return count.isEmpty() ? "Немає даних"
                : Collections.max(count.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Property propertyWithMostRooms() {
        if (properties.isEmpty()) return null;
        return Collections.max(properties, Comparator.comparingInt(Property::getRooms));
    }

    public List<String> saleAddresses() {
        List<String> result = new ArrayList<>();
        for (Property p : properties) {
            if (p.getAvailability() == AvailabilityStatus.НА_ПРОДАЖ) {
                result.add(p.getAddress());
            }
        }
        return result;
    }

    public List<Property> mostDemandedProperties() {
        List<Property> copy = new ArrayList<>(properties);
        copy.sort(Comparator.comparingInt(Property::getInterestCount).reversed());
        return copy;
    }
}
// Головний клас з графічним інтерфейсом (три вкладки: Рієлтор, Клієнт, Нерухомість)
public class RealEstateApp {
    private final Agency agency = new Agency("Нерухомість Плюс");
    private JFrame frame;

    // Таблиці для кожної вкладки
    private DefaultTableModel realtorModel;
    private JTable realtorTable;

    private DefaultTableModel clientModel;
    private JTable clientTable;

    private DefaultTableModel propertyModel;
    private JTable propertyTable;

    public RealEstateApp() {
        frame = new JFrame("Агентство нерухомості — " + agency.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Вкладки для трьох сутностей
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Рієлтор", buildRealtorPanel());
        tabs.addTab("Клієнт", buildClientPanel());
        tabs.addTab("Нерухомість", buildPropertyPanel());

        // Меню запитів
        JMenuBar menuBar = new JMenuBar();
        JMenu reports = new JMenu("Запити");
        JMenuItem miRealtors = new JMenuItem("Список рієлторів");
        miRealtors.addActionListener(e -> showRealtorsList());
        JMenuItem miClients = new JMenuItem("Список клієнтів");
        miClients.addActionListener(e -> showClientsList());
        JMenuItem miSaleAddresses = new JMenuItem("Список адрес продажу");
        miSaleAddresses.addActionListener(e -> showSaleAddresses());
        JMenuItem miMostRooms = new JMenuItem("Нерухомість з найбільшою кількістю кімнат");
        miMostRooms.addActionListener(e -> showPropertyWithMostRooms());
        JMenuItem miAvgPrice = new JMenuItem("Середня вартість нерухомості");
        miAvgPrice.addActionListener(e -> showAveragePrice());
        JMenuItem miDemand = new JMenuItem("Об'єкти з найбільшим попитом");
        miDemand.addActionListener(e -> showMostDemandedProperties());

        reports.add(miRealtors);
        reports.add(miClients);
        reports.add(miSaleAddresses);
        reports.add(miMostRooms);
        reports.add(miAvgPrice);
        reports.add(miDemand);
        menuBar.add(reports);

        frame.setJMenuBar(menuBar);
        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Панель "Рієлтор": таблиця + кнопки CRUD
    private JPanel buildRealtorPanel() {
        realtorModel = new DefaultTableModel(new Object[]{
                "Назва фірми", "ПІБ", "Адреса", "Телефон", "Тип послуги"
        }, 0);
        realtorTable = new JTable(realtorModel);
        JScrollPane scroll = new JScrollPane(realtorTable);

        // Кнопки для роботи з рієлторами
        JButton add = new JButton("Додати рієлтора");
        add.addActionListener(e -> openRealtorDialog(null, -1));

        JButton edit = new JButton("Редагувати");
        edit.addActionListener(e -> {
            int row = realtorTable.getSelectedRow();
            if (row >= 0) {
                Realtor r = agency.realtors.get(row);
                openRealtorDialog(r, row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть рієлтора.");
        });

        JButton delete = new JButton("Видалити");
        delete.addActionListener(e -> {
            int row = realtorTable.getSelectedRow();
            if (row >= 0) {
                agency.removeRealtor(row);
                realtorModel.removeRow(row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть рієлтора.");
        });

        JPanel controls = new JPanel();
        controls.add(add); controls.add(edit); controls.add(delete);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    // Новий клас для допоміжних методів
    class DialogHelper {
        public static JTextField addField(JDialog dialog, String label, String value) {
            JTextField field = new JTextField(value == null ? "" : value);
            dialog.add(new JLabel(label));
            dialog.add(field);
            return field;
        }
    }

    // Рефакторинг openRealtorDialog
    private void openRealtorDialog(Realtor existing, int row) {
        JDialog d = new JDialog(frame, existing == null ? "Новий рієлтор" : "Редагувати рієлтора", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(6, 2));

        JTextField firm = DialogHelper.addField(d, "Назва фірми:", existing == null ? null : existing.getFirmName());
        JTextField name = DialogHelper.addField(d, "ПІБ:", existing == null ? null : existing.getFullName());
        JTextField address = DialogHelper.addField(d, "Адреса:", existing == null ? null : existing.getAddress());
        JTextField phone = DialogHelper.addField(d, "Телефон:", existing == null ? null : existing.getPhone());

        JComboBox<ServiceType> service = new JComboBox<>(ServiceType.values());
        if (existing != null) service.setSelectedItem(existing.getServiceType());
        d.add(new JLabel("Тип послуги:")); d.add(service);

        JButton ok = new JButton("Зберегти");
        ok.addActionListener(e -> {
            try {
                if (existing == null) {
                    Realtor r = new Realtor(firm.getText(), name.getText(), address.getText(), phone.getText(),
                            (ServiceType) service.getSelectedItem());
                    agency.addRealtor(r);
                    realtorModel.addRow(new Object[]{r.getFirmName(), r.getFullName(), r.getAddress(), r.getPhone(), r.getServiceType()});
                } else {
                    existing.setFirmName(firm.getText());
                    existing.setFullName(name.getText());
                    existing.setAddress(address.getText());
                    existing.setPhone(phone.getText());
                    existing.setServiceType((ServiceType) service.getSelectedItem());

                    realtorModel.setValueAt(existing.getFirmName(), row, 0);
                    realtorModel.setValueAt(existing.getFullName(), row, 1);
                    realtorModel.setValueAt(existing.getAddress(), row, 2);
                    realtorModel.setValueAt(existing.getPhone(), row, 3);
                    realtorModel.setValueAt(existing.getServiceType(), row, 4);
                }
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Перевірте коректність даних.");
            }
        });
        d.add(new JLabel()); d.add(ok);
        d.setVisible(true);
    }

    // Панель "Клієнт": таблиця + кнопки CRUD + позначення попиту
    private JPanel buildClientPanel() {
        clientModel = new DefaultTableModel(new Object[]{
                "ПІБ", "Адреса", "Телефон", "Тип послуги"
        }, 0);
        clientTable = new JTable(clientModel);
        JScrollPane scroll = new JScrollPane(clientTable);

        // Кнопки для роботи з клієнтами
        JButton add = new JButton("Додати клієнта");
        add.addActionListener(e -> openClientDialog(null, -1));

        JButton edit = new JButton("Редагувати");
        edit.addActionListener(e -> {
            int row = clientTable.getSelectedRow();
            if (row >= 0) {
                Client c = agency.clients.get(row);
                openClientDialog(c, row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть клієнта.");
        });

        JButton delete = new JButton("Видалити");
        delete.addActionListener(e -> {
            int row = clientTable.getSelectedRow();
            if (row >= 0) {
                agency.removeClient(row);
                clientModel.removeRow(row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть клієнта.");
        });

        // Додаткова кнопка: позначення зацікавлення нерухомістю
        JButton interest = new JButton("Позначити зацікавлення нерухомістю");
        interest.addActionListener(e -> markInterestFlow());

        JPanel controls = new JPanel();
        controls.add(add); controls.add(edit); controls.add(delete); controls.add(interest);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    // Діалог додавання/редагування клієнта
    private void openClientDialog(Client existing, int row) {
        JDialog d = new JDialog(frame, existing == null ? "Новий клієнт" : "Редагувати клієнта", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(5, 2));

        JTextField name = new JTextField(existing == null ? "" : existing.getFullName());
        JTextField address = new JTextField(existing == null ? "" : existing.getAddress());
        JTextField phone = new JTextField(existing == null ? "" : existing.getPhone());
        JComboBox<ServiceType> service = new JComboBox<>(ServiceType.values());
        if (existing != null) service.setSelectedItem(existing.getServiceType());

        d.add(new JLabel("ПІБ:")); d.add(name);
        d.add(new JLabel("Адреса:")); d.add(address);
        d.add(new JLabel("Телефон:")); d.add(phone);
        d.add(new JLabel("Тип послуги:")); d.add(service);

        JButton ok = new JButton("Зберегти");
        ok.addActionListener(e -> {
            try {
                if (existing == null) {
                    Client c = new Client(name.getText(), address.getText(), phone.getText(),
                            (ServiceType) service.getSelectedItem());
                    agency.addClient(c);
                    clientModel.addRow(new Object[]{ c.getFullName(), c.getAddress(), c.getPhone(), c.getServiceType() });
                } else {
                    existing.setFullName(name.getText());
                    existing.setAddress(address.getText());
                    existing.setPhone(phone.getText());
                    existing.setServiceType((ServiceType) service.getSelectedItem());

                    clientModel.setValueAt(existing.getFullName(), row, 0);
                    clientModel.setValueAt(existing.getAddress(), row, 1);
                    clientModel.setValueAt(existing.getPhone(), row, 2);
                    clientModel.setValueAt(existing.getServiceType(), row, 3);
                }
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Перевірте коректність даних.");
            }
        });
        d.add(new JLabel()); d.add(ok);
        d.setVisible(true);
    }

    // Метод позначення попиту: клієнт обирає об'єкт нерухомості
    private void markInterestFlow() {
        if (agency.properties.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Немає нерухомості для позначення зацікавлення.");
            return;
        }
        // Формуємо список доступних об'єктів
        String[] items = new String[agency.properties.size()];
        for (int i = 0; i < items.length; i++) {
            Property p = agency.properties.get(i);
            items[i] = p.getType() + " | " + p.getAddress() + " | " + p.getPrice() + "₴";
        }
        // Діалог вибору об'єкта
        String choice = (String) JOptionPane.showInputDialog(frame, "Оберіть об'єкт:",
                "Зацікавлення", JOptionPane.PLAIN_MESSAGE, null, items, items[0]);
        if (choice != null) {
            int idx = Arrays.asList(items).indexOf(choice);
            agency.properties.get(idx).markInterest();
            JOptionPane.showMessageDialog(frame, "Зацікавлення зафіксовано.");
        }
    }

    // Панель "Нерухомість": таблиця + кнопки CRUD + збереження у файл
    private JPanel buildPropertyPanel() {
        propertyModel = new DefaultTableModel(new Object[]{
                "Тип", "Локація", "Адреса", "Ціна", "Статус", "Площа", "Поверх", "Рік", "Кімнат", "Попит"
        }, 0);
        propertyTable = new JTable(propertyModel);
        JScrollPane scroll = new JScrollPane(propertyTable);

        // Кнопки для роботи з об'єктами нерухомості
        JButton add = new JButton("Додати об'єкт");
        add.addActionListener(e -> openPropertyDialog(null, -1));

        JButton edit = new JButton("Редагувати");
        edit.addActionListener(e -> {
            int row = propertyTable.getSelectedRow();
            if (row >= 0) {
                Property p = agency.properties.get(row);
                openPropertyDialog(p, row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть об'єкт.");
        });

        JButton delete = new JButton("Видалити");
        delete.addActionListener(e -> {
            int row = propertyTable.getSelectedRow();
            if (row >= 0) {
                agency.removeProperty(row);
                propertyModel.removeRow(row);
            } else JOptionPane.showMessageDialog(frame, "Оберіть об'єкт.");
        });

        JButton save = new JButton("Зберегти у файл (CSV)");
        save.addActionListener(e -> agency.savePropertiesToFile("properties_data.csv"));

        JPanel controls = new JPanel();
        controls.add(add); controls.add(edit); controls.add(delete); controls.add(save);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    // Діалог додавання/редагування об'єкта нерухомості
    private void openPropertyDialog(Property existing, int row) {
        JDialog d = new JDialog(frame, existing == null ? "Новий об'єкт" : "Редагувати об'єкт", true);
        d.setSize(450, 450);
        d.setLayout(new GridLayout(11, 2));

        JTextField type = new JTextField(existing == null ? "" : existing.getType());
        JTextField location = new JTextField(existing == null ? "" : existing.getLocation());
        JTextField address = new JTextField(existing == null ? "" : existing.getAddress());
        JTextField price = new JTextField(existing == null ? "" : String.valueOf(existing.getPrice()));
        JComboBox<AvailabilityStatus> status = new JComboBox<>(AvailabilityStatus.values());
        if (existing != null) status.setSelectedItem(existing.getAvailability());

        JTextField area = new JTextField(existing == null ? "" : String.valueOf(existing.getArea()));
        JTextField floor = new JTextField(existing == null ? "" : String.valueOf(existing.getFloor()));
        JTextField year = new JTextField(existing == null ? "" : String.valueOf(existing.getYearBuilt()));
        JTextField rooms = new JTextField(existing == null ? "" : String.valueOf(existing.getRooms()));

        d.add(new JLabel("Тип:")); d.add(type);
        d.add(new JLabel("Локація:")); d.add(location);
        d.add(new JLabel("Адреса:")); d.add(address);
        d.add(new JLabel("Ціна:")); d.add(price);
        d.add(new JLabel("Статус:")); d.add(status);
        d.add(new JLabel("Площа:")); d.add(area);
        d.add(new JLabel("Поверх:")); d.add(floor);
        d.add(new JLabel("Рік побудови:")); d.add(year);
        d.add(new JLabel("Кімнат:")); d.add(rooms);

        JButton ok = new JButton("Зберегти");
        ok.addActionListener(e -> {
            try {
                String t = type.getText();
                String loc = location.getText();
                String addr = address.getText();
                double pr = Double.parseDouble(price.getText());
                AvailabilityStatus st = (AvailabilityStatus) status.getSelectedItem();
                double ar = Double.parseDouble(area.getText());
                int fl = Integer.parseInt(floor.getText());
                int yr = Integer.parseInt(year.getText());
                int rm = Integer.parseInt(rooms.getText());

                if (existing == null) {
                    Property p = new Property(t, loc, addr, pr, st, ar, fl, yr, rm);
                    agency.addProperty(p);
                    propertyModel.addRow(new Object[]{
                            p.getType(), p.getLocation(), p.getAddress(), p.getPrice(), p.getAvailability(),
                            p.getArea(), p.getFloor(), p.getYearBuilt(), p.getRooms(), p.getInterestCount()
                    });
                } else {
                    existing.setType(t);
                    existing.setLocation(loc);
                    existing.setAddress(addr);
                    existing.setPrice(pr);
                    existing.setAvailability(st);
                    existing.setArea(ar);
                    existing.setFloor(fl);
                    existing.setYearBuilt(yr);
                    existing.setRooms(rm);

                    propertyModel.setValueAt(existing.getType(), row, 0);
                    propertyModel.setValueAt(existing.getLocation(), row, 1);
                    propertyModel.setValueAt(existing.getAddress(), row, 2);
                    propertyModel.setValueAt(existing.getPrice(), row, 3);
                    propertyModel.setValueAt(existing.getAvailability(), row, 4);
                    propertyModel.setValueAt(existing.getArea(), row, 5);
                    propertyModel.setValueAt(existing.getFloor(), row, 6);
                    propertyModel.setValueAt(existing.getYearBuilt(), row, 7);
                    propertyModel.setValueAt(existing.getRooms(), row, 8);
                    propertyModel.setValueAt(existing.getInterestCount(), row, 9);
                }
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Перевірте коректність числових полів.");
            }
        });
        d.add(new JLabel()); d.add(ok);
        d.setVisible(true);
    }

    // Вивід списку рієлторів
    private void showRealtorsList() {
        if (agency.realtors.isEmpty()) { JOptionPane.showMessageDialog(frame, "Список рієлторів порожній."); return; }
        StringBuilder sb = new StringBuilder("Рієлтори:\n");
        for (Realtor r : agency.realtors) sb.append("- ").append(r.getFirmName()).append(" | ").append(r.getFullName())
                .append(" | ").append(r.getPhone()).append(" | ").append(r.getServiceType()).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    // Вивід списку клієнтів
    private void showClientsList() {
        if (agency.clients.isEmpty()) { JOptionPane.showMessageDialog(frame, "Список клієнтів порожній."); return; }
        StringBuilder sb = new StringBuilder("Клієнти:\n");
        for (Client c : agency.clients) sb.append("- ").append(c.getFullName()).append(" | ").append(c.getPhone())
                .append(" | ").append(c.getServiceType()).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    // Вивід списку адрес продажу (тільки доступні об'єкти)
    private void showSaleAddresses() {
        List<String> addresses = agency.saleAddresses();
        if (addresses.isEmpty()) { JOptionPane.showMessageDialog(frame, "Немає адрес для продажу."); return; }
        StringBuilder sb = new StringBuilder("Адреси продажу:\n");
        for (String a : addresses) sb.append("- ").append(a).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    // Вивід нерухомості з найбільшою кількістю кімнат
    private void showPropertyWithMostRooms() {
        Property p = agency.propertyWithMostRooms();
        if (p == null) { JOptionPane.showMessageDialog(frame, "Немає даних."); return; }
        JOptionPane.showMessageDialog(frame, "Найбільше кімнат:\n" +
                p.getRooms() + " — " + p.getType() + " | " + p.getAddress());
    }

    // Вивід середньої вартості нерухомості
    private void showAveragePrice() {
        double avg = agency.averagePrice();
        JOptionPane.showMessageDialog(frame, "Середня вартість нерухомості: " + String.format("%.2f", avg) + "₴");
    }

    // Вивід об'єктів з найбільшим попитом (сортування за interestCount)
    private void showMostDemandedProperties() {
        List<Property> list = agency.mostDemandedProperties();
        if (list.isEmpty()) { JOptionPane.showMessageDialog(frame, "Немає даних."); return; }
        StringBuilder sb = new StringBuilder("Найбільший попит (зацікавлення):\n");
        for (Property p : list) {
            sb.append("- ").append(p.getInterestCount()).append(" | ").append(p.getType())
                    .append(" | ").append(p.getAddress()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    // Запуск програми
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RealEstateApp::new);
    }
}

