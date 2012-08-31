package de.doridian.yiffbukkit.jail;

import de.doridian.yiffbukkit.componentsystem.Component;

public class JailComponent extends Component {
	public final JailEngine engine;

	public JailComponent() {
		engine = new JailEngine(plugin);
	}
}
