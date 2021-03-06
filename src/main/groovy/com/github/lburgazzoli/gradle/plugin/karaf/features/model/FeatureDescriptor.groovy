/*
 * Copyright (c) 2015, contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.gradle.plugin.karaf.features.model

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.util.ConfigureUtil

import com.github.lburgazzoli.gradle.plugin.karaf.features.KarafFeaturesTaskExtension

/**
 * DSL extension allowing instruction on how to produce a {@code <feature/>} entry
 * in a Karaf features repository file
 *
 * @author Steve Ebersole
 * @author Luca Burgazzoli
 */
class FeatureDescriptor {
	/**
	 * The name to be used in the {@code <feature/>} entry
	 */
	def String name

	/**
	 * The version to be used in the {@code <feature/>} entry
	 */
	def String version

	/**
	 * Optional description for the feature
	 */
	def String description

    /**
     * The project from which the plugin is instantiated
     */
    def Project project

	/**
	 * Any projects to be included in this feature.  We will pick up
	 * all of their {@code configurations.runtime} dependencies and
	 * add them as bundles.  These project runtime configurations are
	 * considered additive to the {@link #bundleDependencies} configurations
	 */
	def Project[] projects

	/**
	 * Any Configurations containing dependencies to apply as bundles
	 * to this feature.  These configurations are considered additive to the
	 * project runtime configurations from {@link #projects}
	 */
	def Configuration[] bundleDependencies

	/**
	 * Any specific bundle instructions to apply within this feature.
	 */
	def BundleInstructionDescriptor[] bundles

	def String[] dependencyFeatureNames = []

	private final KarafFeaturesTaskExtension extension

	FeatureDescriptor(String name, Project project, KarafFeaturesTaskExtension extension) {
		this.extension = extension
		this.name = name
		this.version = project.version
		this.project = project
	}

	def project(Project project) {
		if ( this.projects == null ) {
			this.projects = [ project ]
		} else {
			this.projects += project
		}
	}

	def bundle(String pattern, Closure closure) {
        def descriptor = new BundleInstructionDescriptor(BundleMatcher.from(pattern))
        ConfigureUtil.configure( closure, descriptor )

        if ( bundles == null ) {
            bundles = [ descriptor ]
        } else {
            bundles += descriptor
        }
    }

	def bundle(Closure closure) {
		def descriptor = new ExtendedBundleInstructionDescriptor()
		ConfigureUtil.configure( closure, descriptor )

		if ( bundles == null ) {
			bundles = [ descriptor ]
		} else {
			bundles += descriptor
		}
	}

    public Project[] getProjects() {
        return this.projects != null ? this.projects : [ this.project ]
    }
}
