#include <iostream>
#include <string>
#include <vector>
#include <limits>

struct Project {
    std::string name;
    std::string description;
    std::string tech;
};

class Portfolio {
public:
    Portfolio(std::string name, std::string title, std::string bio, std::string email)
        : name(std::move(name)), title(std::move(title)),
          bio(std::move(bio)), email(std::move(email)) {}

    void addSkill(const std::string& skill) { skills.push_back(skill); }
    void addProject(const Project& p) { projects.push_back(p); }

    void showAbout() const {
        std::cout << "\n=== ABOUT ===\n";
        std::cout << name << " - " << title << "\n\n" << bio << "\n";
    }

    void showSkills() const {
        std::cout << "\n=== SKILLS ===\n";
        for (const auto& s : skills) {
            std::cout << "  * " << s << "\n";
        }
    }

    void showProjects() const {
        std::cout << "\n=== PROJECTS ===\n";
        int i = 1;
        for (const auto& p : projects) {
            std::cout << i++ << ". " << p.name << "\n"
                      << "   " << p.description << "\n"
                      << "   Tech: " << p.tech << "\n\n";
        }
    }

    void showContact() const {
        std::cout << "\n=== CONTACT ===\n";
        std::cout << "Email: " << email << "\n";
    }

private:
    std::string name, title, bio, email;
    std::vector<std::string> skills;
    std::vector<Project> projects;
};

void printMenu() {
    std::cout << "\n---------- MENU ----------\n"
              << "1. About\n"
              << "2. Skills\n"
              << "3. Projects\n"
              << "4. Contact\n"
              << "0. Exit\n"
              << "Choose an option: ";
}

int main() {
    Portfolio me(
        "Your Name",
        "Software Developer",
        "I build web apps and desktop programs using Java, C++ and web technologies.",
        "you@example.com"
    );

    me.addSkill("Java");
    me.addSkill("C++");
    me.addSkill("HTML / CSS / JavaScript");

    me.addProject({"Project One", "A short description of project one.", "Java"});
    me.addProject({"Project Two", "A short description of project two.", "C++"});
    me.addProject({"Project Three", "A short description of project three.", "HTML, JS"});

    int choice = -1;
    while (choice != 0) {
        printMenu();
        if (!(std::cin >> choice)) {
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            std::cout << "Please enter a number.\n";
            choice = -1;
            continue;
        }

        switch (choice) {
            case 1: me.showAbout(); break;
            case 2: me.showSkills(); break;
            case 3: me.showProjects(); break;
            case 4: me.showContact(); break;
            case 0: std::cout << "Goodbye!\n"; break;
            default: std::cout << "Invalid option.\n";
        }
    }
    return 0;
}