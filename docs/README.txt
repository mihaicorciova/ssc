Code Style:
- when using IDEs other than Eclipse, the util/format_code.bat script must be called before committing files to ClearCase, to avoid merge problems:
	- go into Eclipse and enable Project Specific Settings for your code style. This will generate the .settings/org.eclipse.jdt.core.prefs in your project
	- configure the format_code.bat file to point to your Eclipse IDE executable
	- run the script before every commit