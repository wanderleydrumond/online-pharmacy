package activator;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.annotation.FacesConfig.Version;

/**
 * The purpose of this class is to enable CDI injection of JSF 2.3 beans
 * Do not change or delete it
 */
@FacesConfig(
version = Version.JSF_2_3
)
@ApplicationScoped
public class JSFActivator {
}